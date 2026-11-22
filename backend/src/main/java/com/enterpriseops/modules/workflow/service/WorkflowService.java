package com.enterpriseops.modules.workflow.service;

import com.enterpriseops.modules.employee.entity.Department;
import com.enterpriseops.modules.employee.entity.Employee;
import com.enterpriseops.modules.employee.repository.DepartmentRepository;
import com.enterpriseops.modules.employee.repository.EmployeeRepository;
import com.enterpriseops.modules.service.entity.ServiceTicket;
import com.enterpriseops.modules.service.repository.ServiceTicketRepository;
import com.enterpriseops.modules.workflow.entity.WorkflowDefinition;
import com.enterpriseops.modules.workflow.entity.WorkflowExecution;
import com.enterpriseops.modules.workflow.repository.WorkflowDefinitionRepository;
import com.enterpriseops.modules.workflow.repository.WorkflowExecutionRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WorkflowService {

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final ServiceTicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WorkflowService(WorkflowDefinitionRepository definitionRepository,
                           WorkflowExecutionRepository executionRepository,
                           ServiceTicketRepository ticketRepository,
                           EmployeeRepository employeeRepository,
                           SimpMessagingTemplate messagingTemplate) {
        this.definitionRepository = definitionRepository;
        this.executionRepository = executionRepository;
        this.ticketRepository = ticketRepository;
        this.employeeRepository = employeeRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<WorkflowDefinition> getAllDefinitions() {
        return definitionRepository.findAll();
    }

    public List<WorkflowExecution> getAllExecutions() {
        return executionRepository.findAll();
    }

    /**
     * Standout Feature Execution:
     * 1. Employee submits a request
     * 2. System automatically assigns responsible team
     * 3. Starts SLA timer
     * 4. Updates workflow and opens linked Service Ticket
     * 5. Emits real-time WebSocket alert
     */
    public WorkflowExecution submitRequest(Long employeeId, Long workflowDefId, String title, String payloadJson) {
        Employee requester = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        WorkflowDefinition def = definitionRepository.findById(workflowDefId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow Definition not found: " + workflowDefId));

        LocalDateTime now = LocalDateTime.now();
        int sla = (def.getDefaultSlaMinutes() != null) ? def.getDefaultSlaMinutes() : 120;
        LocalDateTime deadline = now.plusMinutes(sla);

        String execCode = "WF-" + System.currentTimeMillis() % 100000;

        WorkflowExecution execution = new WorkflowExecution(
                execCode, def, title, payloadJson,
                "AUTO_ASSIGNED_TO_" + def.getAutoAssignDepartment().getCode(),
                WorkflowExecution.ExecutionStatus.IN_PROGRESS,
                requester, now, deadline
        );
        WorkflowExecution savedExec = executionRepository.save(execution);

        // Auto-create linked ServiceOps Ticket
        String ticketCode = "TKT-" + (1000 + (int)(Math.random() * 9000));
        ServiceTicket ticket = new ServiceTicket(
                ticketCode, title, payloadJson, def.getName(),
                sla <= 60 ? ServiceTicket.Priority.P1_CRITICAL : ServiceTicket.Priority.P2_HIGH,
                requester, def.getAutoAssignDepartment(), null,
                ServiceTicket.TicketStatus.OPEN, sla, now, deadline, false, null
        );
        ticketRepository.save(ticket);

        // Broadcast real-time notification
        broadcastAlert("NEW_REQUEST_ASSIGNED", "Request " + execCode + " auto-assigned to " + def.getAutoAssignDepartment().getName());

        return savedExec;
    }

    public WorkflowExecution approveStep(Long executionId, String actor) {
        WorkflowExecution exec = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));
        exec.setCurrentStep("APPROVED_BY_" + actor);
        exec.setStatus(WorkflowExecution.ExecutionStatus.APPROVED);
        exec.setCompletedAt(LocalDateTime.now());
        WorkflowExecution updated = executionRepository.save(exec);

        broadcastAlert("REQUEST_APPROVED", "Workflow " + exec.getExecutionCode() + " approved by " + actor);
        return updated;
    }

    public WorkflowExecution escalate(Long executionId, String reason) {
        WorkflowExecution exec = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));
        exec.setStatus(WorkflowExecution.ExecutionStatus.ESCALATED);
        exec.setCurrentStep("ESCALATED_TO_EXECUTIVE_LEAD");
        WorkflowExecution updated = executionRepository.save(exec);

        broadcastAlert("SLA_BREACH_ESCALATED", "CRITICAL SLA Breach on " + exec.getExecutionCode() + ": " + reason);
        return updated;
    }

    private void broadcastAlert(String type, String message) {
        try {
            messagingTemplate.convertAndSend("/topic/workflow-events", "{\"type\":\"" + type + "\",\"message\":\"" + message + "\"}");
        } catch (Exception ignored) {}
    }
}

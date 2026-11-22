package com.enterpriseops.modules.scheduler;

import com.enterpriseops.modules.service.entity.ServiceTicket;
import com.enterpriseops.modules.service.repository.ServiceTicketRepository;
import com.enterpriseops.modules.workflow.entity.WorkflowExecution;
import com.enterpriseops.modules.workflow.repository.WorkflowExecutionRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SlaEscalationScheduler {

    private final ServiceTicketRepository ticketRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public SlaEscalationScheduler(ServiceTicketRepository ticketRepository,
                                  WorkflowExecutionRepository executionRepository,
                                  SimpMessagingTemplate messagingTemplate) {
        this.ticketRepository = ticketRepository;
        this.executionRepository = executionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 15000) // Runs every 15 seconds
    @Transactional
    public void scanAndEscalateBreachedSlas() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Check Service Tickets
        List<ServiceTicket> overdueTickets = ticketRepository.findByDeadlineTimestampBeforeAndStatusNot(
                now, ServiceTicket.TicketStatus.RESOLVED);

        for (ServiceTicket ticket : overdueTickets) {
            if (ticket.getStatus() != ServiceTicket.TicketStatus.ESCALATED && !Boolean.TRUE.equals(ticket.getIsEscalated())) {
                ticket.setIsEscalated(true);
                ticket.setStatus(ServiceTicket.TicketStatus.ESCALATED);
                ticket.setEscalationReason("Automated SLA countdown timer breached. Escalated to department lead.");
                ticketRepository.save(ticket);

                broadcast("TICKET_SLA_BREACHED", "Ticket " + ticket.getTicketCode() + " escalated automatically!");
            }
        }

        // 2. Check Workflow Executions
        List<WorkflowExecution> overdueExecs = executionRepository.findByDeadlineTimestampBeforeAndStatus(
                now, WorkflowExecution.ExecutionStatus.IN_PROGRESS);

        for (WorkflowExecution exec : overdueExecs) {
            exec.setStatus(WorkflowExecution.ExecutionStatus.ESCALATED);
            exec.setCurrentStep("AUTO_ESCALATED_SLA_BREACH");
            executionRepository.save(exec);

            broadcast("WORKFLOW_SLA_BREACHED", "Workflow " + exec.getExecutionCode() + " breached SLA countdown!");
        }
    }

    private void broadcast(String type, String message) {
        try {
            messagingTemplate.convertAndSend("/topic/workflow-events", "{\"type\":\"" + type + "\",\"message\":\"" + message + "\"}");
        } catch (Exception ignored) {}
    }
}

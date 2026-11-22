package com.enterpriseops.seed;

import com.enterpriseops.modules.employee.entity.*;
import com.enterpriseops.modules.employee.repository.*;
import com.enterpriseops.modules.service.entity.ServiceTicket;
import com.enterpriseops.modules.service.repository.ServiceTicketRepository;
import com.enterpriseops.modules.workflow.entity.WorkflowDefinition;
import com.enterpriseops.modules.workflow.entity.WorkflowExecution;
import com.enterpriseops.modules.workflow.repository.WorkflowDefinitionRepository;
import com.enterpriseops.modules.workflow.repository.WorkflowExecutionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class EnterpriseDataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftScheduleRepository shiftRepository;
    private final LeaveRequestRepository leaveRepository;
    private final ServiceTicketRepository ticketRepository;
    private final WorkflowDefinitionRepository defRepository;
    private final WorkflowExecutionRepository execRepository;

    public EnterpriseDataInitializer(DepartmentRepository departmentRepository,
                                     EmployeeRepository employeeRepository,
                                     ShiftScheduleRepository shiftRepository,
                                     LeaveRequestRepository leaveRepository,
                                     ServiceTicketRepository ticketRepository,
                                     WorkflowDefinitionRepository defRepository,
                                     WorkflowExecutionRepository execRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
        this.leaveRepository = leaveRepository;
        this.ticketRepository = ticketRepository;
        this.defRepository = defRepository;
        this.execRepository = execRepository;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) return;

        // 1. Departments
        Department d1 = departmentRepository.save(new Department("IT-INFRA", "IT Infrastructure & DevOps", "Sarah Jenkins", "s.jenkins@enterprise.com"));
        Department d2 = departmentRepository.save(new Department("HR-OPS", "Human Resources & Payroll", "Michael Chang", "m.chang@enterprise.com"));
        Department d3 = departmentRepository.save(new Department("SEC-OPS", "Cybersecurity & IAM", "Robert Mueller", "r.mueller@enterprise.com"));
        Department d4 = departmentRepository.save(new Department("FAC-MGMT", "Workplace & Facilities", "Emily Watson", "e.watson@enterprise.com"));

        // 2. Employees
        Employee e1 = employeeRepository.save(new Employee("EMP-101", "Sarah Jenkins", "s.jenkins@enterprise.com", d1, Employee.Role.DEPARTMENT_LEAD));
        Employee e2 = employeeRepository.save(new Employee("EMP-102", "Alex Mercer", "a.mercer@enterprise.com", d1, Employee.Role.TECHNICIAN));
        Employee e3 = employeeRepository.save(new Employee("EMP-103", "Michael Chang", "m.chang@enterprise.com", d2, Employee.Role.DEPARTMENT_LEAD));
        Employee e4 = employeeRepository.save(new Employee("EMP-104", "David Miller", "d.miller@enterprise.com", d1, Employee.Role.EMPLOYEE));
        Employee e5 = employeeRepository.save(new Employee("EMP-105", "Elena Rostova", "e.rostova@enterprise.com", d2, Employee.Role.EMPLOYEE));

        // 3. Shift Schedules
        LocalDate today = LocalDate.now();
        shiftRepository.save(new ShiftSchedule(e2, today, ShiftSchedule.ShiftType.MORNING_PRIMARY, "L2 DevOps and Ticket Lead"));
        shiftRepository.save(new ShiftSchedule(e1, today, ShiftSchedule.ShiftType.AFTERNOON_SHIFT, "Management Escalation Lead"));
        shiftRepository.save(new ShiftSchedule(e4, today, ShiftSchedule.ShiftType.ON_CALL_SUPPORT, "PagerDuty primary on-call"));

        // 4. Leave Request
        leaveRepository.save(new LeaveRequest(e4, today.plusDays(5), today.plusDays(8), LeaveRequest.LeaveType.ANNUAL, LeaveRequest.LeaveStatus.APPROVED, "Family vacation"));
        leaveRepository.save(new LeaveRequest(e5, today.plusDays(1), today.plusDays(2), LeaveRequest.LeaveType.SICK, LeaveRequest.LeaveStatus.PENDING, "Medical checkup"));

        // 5. Workflow Definitions
        WorkflowDefinition wf1 = defRepository.save(new WorkflowDefinition("WF-VPN-ACCESS", "Production VPN & Bastion Host Provisioning",
                "EMPLOYEE_SUBMIT", d1, 45, "Automated access granting with manager authorization and active 45-min SLA."));

        WorkflowDefinition wf2 = defRepository.save(new WorkflowDefinition("WF-EQUIP-REQ", "Developer Laptop & Hardware Requisition",
                "EMPLOYEE_SUBMIT", d1, 240, "Hardware intake, spec validation, and budget approval workflow."));

        WorkflowDefinition wf3 = defRepository.save(new WorkflowDefinition("WF-LEAVE-APPROVAL", "Special Leave & Sabbatical Review",
                "EMPLOYEE_SUBMIT", d2, 480, "Multi-tier HR review and shift replacement coordination."));

        // 6. Service Tickets
        LocalDateTime now = LocalDateTime.now();
        ticketRepository.save(new ServiceTicket("TKT-8801", "Production Bastion VPN Gateway Auth Failure",
                "Multiple developers unable to authenticate via SSO to AWS US-East VPC.", "IT-Infrastructure",
                ServiceTicket.Priority.P1_CRITICAL, e4, d1, e2, ServiceTicket.TicketStatus.IN_PROGRESS,
                45, now.minusMinutes(15), now.plusMinutes(30), false, null));

        // Overdue Ticket (triggers escalation!)
        ticketRepository.save(new ServiceTicket("TKT-7702", "PostgreSQL Read Replica Replication Lag Alert",
                "Replication lag on analytics replica exceeded 120 seconds.", "Database-Ops",
                ServiceTicket.Priority.P1_CRITICAL, e4, d1, e2, ServiceTicket.TicketStatus.OPEN,
                30, now.minusMinutes(45), now.minusMinutes(15), true, "Automatic SLA countdown timer breached. Escalated to department lead."));

        ticketRepository.save(new ServiceTicket("TKT-5503", "Request for JetBrains All Products License",
                "License allocation for incoming backend engineering cohort.", "Software-Tooling",
                ServiceTicket.Priority.P3_MEDIUM, e5, d1, e1, ServiceTicket.TicketStatus.RESOLVED,
                1440, now.minusDays(1), now.plusHours(12), false, null));

        // 7. Workflow Executions
        execRepository.save(new WorkflowExecution("WF-EXEC-991", wf1, "VPN Access for Contractor Data Analyst",
                "{\"user\":\"contractor_da\",\"tier\":\"read_only\"}", "AUTO_ASSIGNED_TO_IT-INFRA",
                WorkflowExecution.ExecutionStatus.IN_PROGRESS, e4, now.minusMinutes(10), now.plusMinutes(35)));

        execRepository.save(new WorkflowExecution("WF-EXEC-992", wf2, "MacBook Pro M3 Max Requisition",
                "{\"user\":\"alex_m\",\"ram\":\"64GB\"}", "AUTO_ESCALATED_SLA_BREACH",
                WorkflowExecution.ExecutionStatus.ESCALATED, e2, now.minusHours(6), now.minusHours(2)));
    }
}

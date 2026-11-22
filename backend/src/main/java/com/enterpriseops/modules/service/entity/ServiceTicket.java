package com.enterpriseops.modules.service.entity;

import com.enterpriseops.modules.employee.entity.Department;
import com.enterpriseops.modules.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_tickets")
public class ServiceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketCode;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id")
    private Employee requester;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department assignedDepartment;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Employee assignee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    private Integer slaMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime deadlineTimestamp;
    private Boolean isEscalated;
    private String escalationReason;

    public enum Priority {
        P1_CRITICAL,
        P2_HIGH,
        P3_MEDIUM,
        P4_LOW
    }

    public enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        ESCALATED,
        CLOSED
    }

    public ServiceTicket() {}

    public ServiceTicket(String ticketCode, String title, String description, String category,
                         Priority priority, Employee requester, Department assignedDepartment,
                         Employee assignee, TicketStatus status, Integer slaMinutes,
                         LocalDateTime createdAt, LocalDateTime deadlineTimestamp,
                         Boolean isEscalated, String escalationReason) {
        this.ticketCode = ticketCode;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.requester = requester;
        this.assignedDepartment = assignedDepartment;
        this.assignee = assignee;
        this.status = status;
        this.slaMinutes = slaMinutes;
        this.createdAt = createdAt;
        this.deadlineTimestamp = deadlineTimestamp;
        this.isEscalated = isEscalated;
        this.escalationReason = escalationReason;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Employee getRequester() { return requester; }
    public void setRequester(Employee requester) { this.requester = requester; }

    public Department getAssignedDepartment() { return assignedDepartment; }
    public void setAssignedDepartment(Department assignedDepartment) { this.assignedDepartment = assignedDepartment; }

    public Employee getAssignee() { return assignee; }
    public void setAssignee(Employee assignee) { this.assignee = assignee; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public Integer getSlaMinutes() { return slaMinutes; }
    public void setSlaMinutes(Integer slaMinutes) { this.slaMinutes = slaMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeadlineTimestamp() { return deadlineTimestamp; }
    public void setDeadlineTimestamp(LocalDateTime deadlineTimestamp) { this.deadlineTimestamp = deadlineTimestamp; }

    public Boolean getIsEscalated() { return isEscalated; }
    public void setIsEscalated(Boolean isEscalated) { this.isEscalated = isEscalated; }

    public String getEscalationReason() { return escalationReason; }
    public void setEscalationReason(String escalationReason) { this.escalationReason = escalationReason; }
}

package com.enterpriseops.modules.workflow.entity;

import com.enterpriseops.modules.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_executions")
public class WorkflowExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String executionCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workflow_def_id")
    private WorkflowDefinition workflowDefinition;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String payloadJson;

    @Column(nullable = false)
    private String currentStep; // E.g. "AUTO_ASSIGNED_TO_TEAM", "SLA_TIMER_ACTIVE", "MANAGER_REVIEW", "COMPLETED"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "initiated_by_id")
    private Employee initiatedBy;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime deadlineTimestamp;
    private LocalDateTime completedAt;

    public enum ExecutionStatus {
        IN_PROGRESS,
        APPROVED,
        ESCALATED,
        REJECTED
    }

    public WorkflowExecution() {}

    public WorkflowExecution(String executionCode, WorkflowDefinition workflowDefinition, String title,
                             String payloadJson, String currentStep, ExecutionStatus status,
                             Employee initiatedBy, LocalDateTime startedAt, LocalDateTime deadlineTimestamp) {
        this.executionCode = executionCode;
        this.workflowDefinition = workflowDefinition;
        this.title = title;
        this.payloadJson = payloadJson;
        this.currentStep = currentStep;
        this.status = status;
        this.initiatedBy = initiatedBy;
        this.startedAt = startedAt;
        this.deadlineTimestamp = deadlineTimestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExecutionCode() { return executionCode; }
    public void setExecutionCode(String executionCode) { this.executionCode = executionCode; }

    public WorkflowDefinition getWorkflowDefinition() { return workflowDefinition; }
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) { this.workflowDefinition = workflowDefinition; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }

    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }

    public Employee getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(Employee initiatedBy) { this.initiatedBy = initiatedBy; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getDeadlineTimestamp() { return deadlineTimestamp; }
    public void setDeadlineTimestamp(LocalDateTime deadlineTimestamp) { this.deadlineTimestamp = deadlineTimestamp; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}

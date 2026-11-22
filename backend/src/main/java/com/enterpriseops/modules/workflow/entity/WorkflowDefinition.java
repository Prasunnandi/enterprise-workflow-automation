package com.enterpriseops.modules.workflow.entity;

import com.enterpriseops.modules.employee.entity.Department;
import jakarta.persistence.*;

@Entity
@Table(name = "workflow_definitions")
public class WorkflowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String workflowCode;

    @Column(nullable = false)
    private String name;

    private String triggerEvent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "auto_assign_dept_id")
    private Department autoAssignDepartment;

    private Integer defaultSlaMinutes;
    private String description;

    public WorkflowDefinition() {}

    public WorkflowDefinition(String workflowCode, String name, String triggerEvent,
                              Department autoAssignDepartment, Integer defaultSlaMinutes, String description) {
        this.workflowCode = workflowCode;
        this.name = name;
        this.triggerEvent = triggerEvent;
        this.autoAssignDepartment = autoAssignDepartment;
        this.defaultSlaMinutes = defaultSlaMinutes;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWorkflowCode() { return workflowCode; }
    public void setWorkflowCode(String workflowCode) { this.workflowCode = workflowCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTriggerEvent() { return triggerEvent; }
    public void setTriggerEvent(String triggerEvent) { this.triggerEvent = triggerEvent; }

    public Department getAutoAssignDepartment() { return autoAssignDepartment; }
    public void setAutoAssignDepartment(Department autoAssignDepartment) { this.autoAssignDepartment = autoAssignDepartment; }

    public Integer getDefaultSlaMinutes() { return defaultSlaMinutes; }
    public void setDefaultSlaMinutes(Integer defaultSlaMinutes) { this.defaultSlaMinutes = defaultSlaMinutes; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

package com.enterpriseops.modules.workflow.controller;

import com.enterpriseops.modules.workflow.entity.WorkflowDefinition;
import com.enterpriseops.modules.workflow.entity.WorkflowExecution;
import com.enterpriseops.modules.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@Tag(name = "OpsPilot - Business Process Automation", description = "Endpoints for defining workflows, initiating requests, tracking SLA timers, and approvals")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/definitions")
    @Operation(summary = "List all registered workflow definitions")
    public ResponseEntity<List<WorkflowDefinition>> getDefinitions() {
        return ResponseEntity.ok(workflowService.getAllDefinitions());
    }

    @GetMapping("/executions")
    @Operation(summary = "List all workflow executions and active SLA timers")
    public ResponseEntity<List<WorkflowExecution>> getExecutions() {
        return ResponseEntity.ok(workflowService.getAllExecutions());
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit a request (Auto-assigns team, starts SLA countdown, creates ticket)")
    public ResponseEntity<WorkflowExecution> submitRequest(@RequestParam Long employeeId,
                                                           @RequestParam Long workflowDefId,
                                                           @RequestParam String title,
                                                           @RequestBody(required = false) String payloadJson) {
        return ResponseEntity.ok(workflowService.submitRequest(employeeId, workflowDefId, title, payloadJson != null ? payloadJson : "{}"));
    }

    @PostMapping("/executions/{id}/approve")
    @Operation(summary = "Approve and advance a workflow step")
    public ResponseEntity<WorkflowExecution> approveStep(@PathVariable Long id, @RequestParam(defaultValue = "Department Lead") String actor) {
        return ResponseEntity.ok(workflowService.approveStep(id, actor));
    }

    @PostMapping("/executions/{id}/escalate")
    @Operation(summary = "Manually or automatically escalate overdue workflow request")
    public ResponseEntity<WorkflowExecution> escalate(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(workflowService.escalate(id, reason));
    }
}

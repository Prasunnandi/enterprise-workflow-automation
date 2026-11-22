package com.enterpriseops.modules.workflow.repository;

import com.enterpriseops.modules.workflow.entity.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
    Optional<WorkflowDefinition> findByWorkflowCode(String workflowCode);
}

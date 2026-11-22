package com.enterpriseops.modules.workflow.repository;

import com.enterpriseops.modules.workflow.entity.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {
    Optional<WorkflowExecution> findByExecutionCode(String executionCode);
    List<WorkflowExecution> findByStatus(WorkflowExecution.ExecutionStatus status);
    List<WorkflowExecution> findByDeadlineTimestampBeforeAndStatus(LocalDateTime time, WorkflowExecution.ExecutionStatus status);
}

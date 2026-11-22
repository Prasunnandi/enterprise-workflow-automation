package com.enterpriseops.modules.service.repository;

import com.enterpriseops.modules.service.entity.ServiceTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {
    Optional<ServiceTicket> findByTicketCode(String ticketCode);
    List<ServiceTicket> findByStatus(ServiceTicket.TicketStatus status);
    List<ServiceTicket> findByPriority(ServiceTicket.Priority priority);
    List<ServiceTicket> findByAssignedDepartmentId(Long departmentId);
    List<ServiceTicket> findByDeadlineTimestampBeforeAndStatusNot(LocalDateTime time, ServiceTicket.TicketStatus status);
}

package com.enterpriseops.modules.service.service;

import com.enterpriseops.modules.service.entity.ServiceTicket;
import com.enterpriseops.modules.service.repository.ServiceTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceTicketService {

    private final ServiceTicketRepository ticketRepository;

    public ServiceTicketService(ServiceTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<ServiceTicket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public ServiceTicket createTicket(ServiceTicket ticket) {
        return ticketRepository.save(ticket);
    }

    public ServiceTicket updateStatus(Long id, ServiceTicket.TicketStatus status) {
        ServiceTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }
}

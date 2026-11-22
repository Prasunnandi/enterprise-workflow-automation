package com.enterpriseops.modules.service.controller;

import com.enterpriseops.modules.service.entity.ServiceTicket;
import com.enterpriseops.modules.service.service.ServiceTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "ServiceOps - IT Service Management", description = "Endpoints for issue ticketing, priorities (P1-P4), assignments, and SLA resolution")
public class ServiceTicketController {

    private final ServiceTicketService ticketService;

    public ServiceTicketController(ServiceTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @Operation(summary = "List all IT service tickets")
    public ResponseEntity<List<ServiceTicket>> getTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PostMapping
    @Operation(summary = "Create an issue ticket")
    public ResponseEntity<ServiceTicket> createTicket(@RequestBody ServiceTicket ticket) {
        return ResponseEntity.ok(ticketService.createTicket(ticket));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update ticket resolution status")
    public ResponseEntity<ServiceTicket> updateStatus(@PathVariable Long id, @RequestParam ServiceTicket.TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateStatus(id, status));
    }
}

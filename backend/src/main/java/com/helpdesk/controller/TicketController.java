package com.helpdesk.controller;

import com.helpdesk.dto.*;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;

    public TicketController(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<TicketDto> createTicket(@Valid @RequestBody CreateTicketRequest request,
                                                 Authentication authentication) {
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);
        TicketDto ticket = ticketService.createTicket(request, userId);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TicketDto>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TicketDto> tickets = ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Page<TicketDto>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Ticket.TicketStatus status,
            @RequestParam(required = false) Ticket.Priority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long assignedToId,
            Authentication authentication) {
        
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // If filters are provided, use search method
        if (title != null || status != null || priority != null || categoryId != null || assignedToId != null) {
            Page<TicketDto> tickets = ticketService.searchUserTickets(
                userId, title, status, priority, categoryId, assignedToId, pageable);
            return ResponseEntity.ok(tickets);
        } else {
            Page<TicketDto> tickets = ticketService.getTicketsByUser(userId, pageable);
            return ResponseEntity.ok(tickets);
        }
    }

    @GetMapping("/my/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Page<TicketDto>> searchMyTickets(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Ticket.TicketStatus status,
            @RequestParam(required = false) Ticket.Priority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TicketDto> tickets = ticketService.searchUserTickets(
            userId, title, status, priority, categoryId, assignedToId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Page<TicketDto>> getAssignedTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TicketDto> tickets = ticketService.getAssignedTickets(userId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Page<TicketDto>> searchTickets(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Ticket.TicketStatus status,
            @RequestParam(required = false) Ticket.Priority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TicketDto> tickets = ticketService.searchTickets(
            title, status, priority, categoryId, assignedToId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Long id) {
        TicketDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable Long id, 
                                                 @Valid @RequestBody UpdateTicketRequest request) {
        TicketDto ticket = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<TicketDto> assignTicket(@PathVariable Long ticketId, 
                                                 @RequestParam Long assignedToId) {
        TicketDto ticket = ticketService.assignTicket(ticketId, assignedToId);
        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketStatsDto> getTicketStatsOverview() {
        TicketStatsDto stats = new TicketStatsDto();
        stats.setTotal(ticketService.getTotalTicketCount());
        stats.setNewTickets(ticketService.getTicketCountByStatus(Ticket.TicketStatus.NEW));
        stats.setInProgress(ticketService.getTicketCountByStatus(Ticket.TicketStatus.IN_PROGRESS));
        stats.setResolved(ticketService.getTicketCountByStatus(Ticket.TicketStatus.RESOLVED));
        stats.setClosed(ticketService.getTicketCountByStatus(Ticket.TicketStatus.CLOSED));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<List<TicketStatusCount>> getTicketStatsByStatus() {
        List<TicketStatusCount> stats = List.of(
            new TicketStatusCount("NEW", ticketService.getTicketCountByStatus(Ticket.TicketStatus.NEW)),
            new TicketStatusCount("IN_PROGRESS", ticketService.getTicketCountByStatus(Ticket.TicketStatus.IN_PROGRESS)),
            new TicketStatusCount("RESOLVED", ticketService.getTicketCountByStatus(Ticket.TicketStatus.RESOLVED)),
            new TicketStatusCount("CLOSED", ticketService.getTicketCountByStatus(Ticket.TicketStatus.CLOSED))
        );
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<TicketStatsDto> getMyTicketStats(Authentication authentication) {
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);
        
        TicketStatsDto stats = new TicketStatsDto();
        stats.setTotal(ticketService.getTicketCountByUserId(userId));
        stats.setNewTickets(ticketService.getTicketCountByStatusAndUser(Ticket.TicketStatus.NEW, userId));
        stats.setInProgress(ticketService.getTicketCountByStatusAndUser(Ticket.TicketStatus.IN_PROGRESS, userId));
        stats.setResolved(ticketService.getTicketCountByStatusAndUser(Ticket.TicketStatus.RESOLVED, userId));
        stats.setClosed(ticketService.getTicketCountByStatusAndUser(Ticket.TicketStatus.CLOSED, userId));
        stats.setMyTickets(ticketService.getTicketCountByUserId(userId));
        stats.setAssignedToMe(ticketService.getTicketCountByAssignedUserId(userId));
        
        return ResponseEntity.ok(stats);
    }

    private Long getUserIdFromUsername(String username) {
        // Tymczasowo na sztywno zwracamy ID 1 dla admina
        if ("admin".equals(username)) {
            return 1L;
        }
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public static class TicketStatusCount {
        private String status;
        private long count;

        public TicketStatusCount(String status, long count) {
            this.status = status;
            this.count = count;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}

package com.helpdesk.service;

import com.helpdesk.dto.*;
import com.helpdesk.entity.*;
import com.helpdesk.mapper.TicketMapper;
import com.helpdesk.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;
    private final TicketHistoryRepository ticketHistoryRepository;

    public TicketService(TicketRepository ticketRepository, CategoryRepository categoryRepository,
                        UserRepository userRepository, TicketMapper ticketMapper, 
                        TicketHistoryRepository ticketHistoryRepository) {
        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    public TicketDto createTicket(CreateTicketRequest request, Long createdByUserId) {
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCategory(category);
        ticket.setCreatedBy(createdBy);
        ticket.setStatus(Ticket.TicketStatus.NEW);

        // Handle assignment
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            ticket.setAssignedTo(assignedTo);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }

    @Transactional(readOnly = true)
    public Page<TicketDto> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable)
                .map(ticketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TicketDto> getTicketsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByCreatedBy(user, pageable)
                .map(ticketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TicketDto> getAssignedTickets(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByAssignedTo(user, pageable)
                .map(ticketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TicketDto> searchTickets(String title, Ticket.TicketStatus status, 
                                        Ticket.Priority priority, Long categoryId, 
                                        Long assignedToId, Pageable pageable) {
        User assignedTo = assignedToId != null ? 
            userRepository.findById(assignedToId).orElse(null) : null;
        
        return ticketRepository.findByFilters(title, status, priority, categoryId, assignedTo, pageable)
                .map(ticketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TicketDto> searchUserTickets(Long userId, String title, Ticket.TicketStatus status, 
                                           Ticket.Priority priority, Long categoryId, 
                                           Long assignedToId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User assignedTo = assignedToId != null ? 
            userRepository.findById(assignedToId).orElse(null) : null;
        
        return ticketRepository.findByUserFilters(user, title, status, priority, categoryId, assignedTo, pageable)
                .map(ticketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TicketDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ticketMapper.toDto(ticket);
    }

    public TicketDto updateTicket(Long id, UpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            ticket.setCategory(category);
        }
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElse(null);
            ticket.setAssignedTo(assignedTo);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    public long getTicketCountByStatus(Ticket.TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    public long getTotalTicketCount() {
        return ticketRepository.count();
    }

    public long getTicketCountByStatusAndUser(Ticket.TicketStatus status, Long userId) {
        return ticketRepository.countByStatusAndCreatedBy(status, userId);
    }

    public long getTicketCountByUserId(Long userId) {
        return ticketRepository.countByCreatedById(userId);
    }

    public long getTicketCountByAssignedUserId(Long userId) {
        return ticketRepository.countByAssignedToId(userId);
    }

    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found");
        }
        ticketRepository.deleteById(id);
    }

    public TicketDto assignTicket(Long ticketId, Long assignedToId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        User assignedTo = userRepository.findById(assignedToId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ticket.setAssignedTo(assignedTo);
        if (ticket.getStatus() == Ticket.TicketStatus.NEW) {
            ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    public void recordStatusChange(Ticket ticket, Ticket.TicketStatus newStatus, User changedBy, String reason) {
        if (!ticket.getStatus().equals(newStatus)) {
            TicketHistory history = new TicketHistory(ticket, ticket.getStatus(), newStatus, changedBy, reason);
            ticketHistoryRepository.save(history);
        }
    }

    public Duration calculateResolutionTime(Ticket ticket) {
        if (ticket.getCreatedAt() != null && ticket.getResolvedAt() != null) {
            return Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt());
        }
        return null;
    }

    public boolean shouldEscalate(Ticket ticket) {
        if (ticket.getPriority() == Ticket.Priority.CRITICAL && ticket.getStatus() == Ticket.TicketStatus.NEW) {
            return true;
        }
        
        Duration timeSinceCreation = Duration.between(ticket.getCreatedAt(), LocalDateTime.now());
        if (ticket.getPriority() == Ticket.Priority.HIGH && timeSinceCreation.toHours() > 2) {
            return true;
        }
        
        if (ticket.getPriority() == Ticket.Priority.MEDIUM && timeSinceCreation.toHours() > 8) {
            return true;
        }
        
        return false;
    }

    public void escalateTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (shouldEscalate(ticket)) {
            ticket.setPriority(Ticket.Priority.CRITICAL);
            recordStatusChange(ticket, ticket.getStatus(), ticket.getCreatedBy(), "Auto-escalated due to priority/time threshold");
            ticketRepository.save(ticket);
        }
    }
}

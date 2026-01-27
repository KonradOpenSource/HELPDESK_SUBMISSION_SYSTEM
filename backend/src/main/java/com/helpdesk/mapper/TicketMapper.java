package com.helpdesk.mapper;

import com.helpdesk.dto.TicketDto;
import com.helpdesk.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDto toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        
        return new TicketDto(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getStatus(),
            ticket.getPriority(),
            ticket.getCategory() != null ? ticket.getCategory().getId() : null,
            ticket.getCategory() != null ? ticket.getCategory().getName() : null,
            ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null,
            ticket.getCreatedBy() != null ? 
                ticket.getCreatedBy().getFirstName() + " " + ticket.getCreatedBy().getLastName() : null,
            ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null,
            ticket.getAssignedTo() != null ? 
                ticket.getAssignedTo().getFirstName() + " " + ticket.getAssignedTo().getLastName() : null,
            ticket.getCreatedAt(),
            ticket.getUpdatedAt(),
            ticket.getResolvedAt()
        );
    }
}

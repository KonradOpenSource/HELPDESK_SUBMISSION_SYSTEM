package com.helpdesk.dto;

import com.helpdesk.entity.Ticket;
import jakarta.validation.constraints.Size;

public class UpdateTicketRequest {

    @Size(max = 200)
    private String title;

    private String description;

    private Ticket.TicketStatus status;

    private Ticket.Priority priority;

    private Long categoryId;

    private Long assignedToId;

    public UpdateTicketRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Ticket.TicketStatus getStatus() { return status; }
    public void setStatus(Ticket.TicketStatus status) { this.status = status; }

    public Ticket.Priority getPriority() { return priority; }
    public void setPriority(Ticket.Priority priority) { this.priority = priority; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
}

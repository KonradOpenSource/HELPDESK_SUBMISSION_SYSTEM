package com.helpdesk.dto;

import com.helpdesk.entity.Ticket;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateTicketRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private Ticket.Priority priority;

    @NotNull(message = "Category is required")
    @Min(value = 1, message = "Category ID must be valid")
    private Long categoryId;

    private Long assignedToId;

    public CreateTicketRequest() {}

    public CreateTicketRequest(String title, String description, Ticket.Priority priority, Long categoryId, Long assignedToId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.categoryId = categoryId;
        this.assignedToId = assignedToId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Ticket.Priority getPriority() { return priority; }
    public void setPriority(Ticket.Priority priority) { this.priority = priority; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
}

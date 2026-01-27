package com.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCommentRequest {

    @NotBlank
    private String content;
    
    @NotNull
    private Long ticketId;

    public CreateCommentRequest() {}

    public CreateCommentRequest(String content, Long ticketId) {
        this.content = content;
        this.ticketId = ticketId;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
}

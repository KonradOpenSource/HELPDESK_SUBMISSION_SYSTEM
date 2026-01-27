package com.helpdesk.dto;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private String content;
    private Long ticketId;
    private Long userId;
    private String username;
    private String userFirstName;
    private String userLastName;
    private LocalDateTime createdAt;

    public CommentDto() {}

    public CommentDto(Long id, String content, Long ticketId, Long userId, String username, 
                     String userFirstName, String userLastName, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.ticketId = ticketId;
        this.userId = userId;
        this.username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }

    public String getUserLastName() { return userLastName; }
    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

package com.helpdesk.mapper;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        return new CommentDto(
            comment.getId(),
            comment.getContent(),
            comment.getTicket() != null ? comment.getTicket().getId() : null,
            comment.getUser() != null ? comment.getUser().getId() : null,
            comment.getUser() != null ? comment.getUser().getUsername() : null,
            comment.getUser() != null ? comment.getUser().getFirstName() : null,
            comment.getUser() != null ? comment.getUser().getLastName() : null,
            comment.getCreatedAt()
        );
    }
}

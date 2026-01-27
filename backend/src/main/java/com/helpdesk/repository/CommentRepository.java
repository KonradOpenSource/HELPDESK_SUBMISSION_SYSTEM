package com.helpdesk.repository;

import com.helpdesk.entity.Comment;
import com.helpdesk.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
    
    List<Comment> findByTicketOrderByCreatedAtDesc(Ticket ticket);
    
    void deleteByTicketId(Long ticketId);
}

package com.helpdesk.service;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.dto.CreateCommentRequest;
import com.helpdesk.entity.Comment;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.mapper.CommentMapper;
import com.helpdesk.repository.CommentRepository;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, TicketRepository ticketRepository,
                         UserRepository userRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    public CommentDto createComment(CreateCommentRequest request, Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTicket(ticket);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        
        // Tymczasowo zwracam prosty DTO bez mapper
        CommentDto dto = new CommentDto();
        dto.setId(savedComment.getId());
        dto.setContent(savedComment.getContent());
        dto.setTicketId(savedComment.getTicket().getId());
        dto.setUserId(savedComment.getUser().getId());
        dto.setUsername(savedComment.getUser().getUsername());
        dto.setCreatedAt(savedComment.getCreatedAt());
        
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByTicketId(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new RuntimeException("Ticket not found");
        }
        return commentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentMapper.toDto(comment);
    }

    public void deleteComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        
        commentRepository.deleteById(id);
    }
}

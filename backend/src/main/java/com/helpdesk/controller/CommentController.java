package com.helpdesk.controller;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.dto.CreateCommentRequest;
import com.helpdesk.entity.User;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    public CommentController(CommentService commentService, UserRepository userRepository) {
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<CommentDto> createComment(@RequestBody CreateCommentRequest request,
                                                  Authentication authentication) {
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);
        CommentDto comment = commentService.createComment(request, request.getTicketId(), userId);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/ticket/{ticketId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<List<CommentDto>> getCommentsByTicketId(@PathVariable Long ticketId) {
        List<CommentDto> comments = commentService.getCommentsByTicketId(ticketId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Long userId = getUserIdFromUsername(username);
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
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
}

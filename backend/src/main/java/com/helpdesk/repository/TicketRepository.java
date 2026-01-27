package com.helpdesk.repository;

import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    Page<Ticket> findByCreatedBy(User createdBy, Pageable pageable);
    
    Page<Ticket> findByAssignedTo(User assignedTo, Pageable pageable);
    
    Page<Ticket> findByStatus(Ticket.TicketStatus status, Pageable pageable);
    
    Page<Ticket> findByPriority(Ticket.Priority priority, Pageable pageable);
    
    Page<Ticket> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
           "(:assignedTo IS NULL OR t.assignedTo = :assignedTo)")
    Page<Ticket> findByFilters(@Param("title") String title,
                              @Param("status") Ticket.TicketStatus status,
                              @Param("priority") Ticket.Priority priority,
                              @Param("categoryId") Long categoryId,
                              @Param("assignedTo") User assignedTo,
                              Pageable pageable);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "t.createdBy = :user AND " +
           "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
           "(:assignedTo IS NULL OR t.assignedTo = :assignedTo)")
    Page<Ticket> findByUserFilters(@Param("user") User user,
                                  @Param("title") String title,
                                  @Param("status") Ticket.TicketStatus status,
                                  @Param("priority") Ticket.Priority priority,
                                  @Param("categoryId") Long categoryId,
                                  @Param("assignedTo") User assignedTo,
                                  Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") Ticket.TicketStatus status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND t.createdBy.id = :userId")
    long countByStatusAndCreatedBy(@Param("status") Ticket.TicketStatus status, @Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo.id = :userId")
    long countByAssignedToId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo = :assignedTo AND t.status IN :statuses")
    long countByAssignedToAndStatusIn(@Param("assignedTo") User assignedTo, 
                                     @Param("statuses") List<Ticket.TicketStatus> statuses);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdBy.id = :userId")
    long countByCreatedById(@Param("userId") Long userId);
}

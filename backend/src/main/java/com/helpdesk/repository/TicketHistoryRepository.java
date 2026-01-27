package com.helpdesk.repository;

import com.helpdesk.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
    
    List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
    
    @Query("SELECT COUNT(th) FROM TicketHistory th WHERE th.ticket.id = :ticketId")
    long countByTicketId(Long ticketId);
    
    @Query("SELECT th FROM TicketHistory th WHERE th.ticket.id = :ticketId AND th.newStatus = :status")
    List<TicketHistory> findByTicketIdAndNewStatus(Long ticketId, String status);
}

package com.helpdesk.service;

import com.helpdesk.dto.CreateTicketRequest;
import com.helpdesk.dto.TicketDto;
import com.helpdesk.dto.UpdateTicketRequest;
import com.helpdesk.entity.Category;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import com.helpdesk.mapper.TicketMapper;
import com.helpdesk.repository.CategoryRepository;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    private TicketMapper ticketMapper = new TicketMapper();

    @InjectMocks
    private TicketService ticketService;

    private User testUser;
    private Category testCategory;
    private Ticket testTicket;
    private CreateTicketRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Technical Support");

        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTitle("Test Ticket");
        testTicket.setDescription("Test Description");
        testTicket.setStatus(Ticket.TicketStatus.NEW);
        testTicket.setPriority(Ticket.Priority.MEDIUM);
        testTicket.setCategory(testCategory);
        testTicket.setCreatedBy(testUser);

        createRequest = new CreateTicketRequest();
        createRequest.setTitle("Test Ticket");
        createRequest.setDescription("Test Description");
        createRequest.setPriority(Ticket.Priority.MEDIUM);
        createRequest.setCategoryId(1L);
    }

    @Test
    void createTicket_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        TicketDto result = ticketService.createTicket(createRequest, 1L);

        assertNotNull(result);
        assertEquals("Test Ticket", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void createTicket_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ticketService.createTicket(createRequest, 1L));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void createTicket_CategoryNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ticketService.createTicket(createRequest, 1L));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void getTicketById_Success() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        TicketDto result = ticketService.getTicketById(1L);

        assertNotNull(result);
        assertEquals("Test Ticket", result.getTitle());
        verify(ticketRepository).findById(1L);
    }

    @Test
    void getTicketById_NotFound_ThrowsException() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ticketService.getTicketById(1L));
    }

    @Test
    void updateTicket_Success() {
        UpdateTicketRequest updateRequest = new UpdateTicketRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setStatus(Ticket.TicketStatus.IN_PROGRESS);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        TicketDto result = ticketService.updateTicket(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, result.getStatus());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void deleteTicket_Success() {
        when(ticketRepository.existsById(1L)).thenReturn(true);

        ticketService.deleteTicket(1L);

        verify(ticketRepository).deleteById(1L);
    }

    @Test
    void deleteTicket_NotFound_ThrowsException() {
        when(ticketRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> ticketService.deleteTicket(1L));
        verify(ticketRepository, never()).deleteById(1L);
    }

    @Test
    void assignTicket_Success() {
        User assignedUser = new User();
        assignedUser.setId(2L);
        assignedUser.setUsername("supportuser");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignedUser));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        TicketDto result = ticketService.assignTicket(1L, 2L);

        assertNotNull(result);
        assertEquals(Ticket.TicketStatus.IN_PROGRESS, result.getStatus());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void getTicketCountByStatus_Success() {
        when(ticketRepository.countByStatus(Ticket.TicketStatus.NEW)).thenReturn(5L);

        long result = ticketService.getTicketCountByStatus(Ticket.TicketStatus.NEW);

        assertEquals(5L, result);
        verify(ticketRepository).countByStatus(Ticket.TicketStatus.NEW);
    }

    @Test
    void getAllTickets_Success() {
        Page<Ticket> ticketPage = new PageImpl<>(List.of(testTicket));
        when(ticketRepository.findAll(any(Pageable.class))).thenReturn(ticketPage);

        Page<TicketDto> result = ticketService.getAllTickets(mock(Pageable.class));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ticketRepository).findAll(any(Pageable.class));
    }
}

package com.mini_project.miniproject.events.service.impl;

import com.mini_project.miniproject.events.dto.PaginatedEventResponseDto;
import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.entity.TicketTiers;
import com.mini_project.miniproject.events.entity.EventVouchers;
import com.mini_project.miniproject.events.repository.EventRepository;
import com.mini_project.miniproject.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Events event1;
    private Events event2;
    private Users organizer;

    @BeforeEach
    void setUp() {
        organizer = new Users();
        organizer.setId(1L);
        organizer.setFirstName("John");
        organizer.setLastName("Doe");
        organizer.setEmail("john.doe@example.com");

        event1 = new Events();
        event1.setId(1L);
        event1.setName("Rock Concert");
        event1.setDescription("A fantastic rock concert");
        event1.setCategory("Music");
        event1.setCity("New York");
        event1.setDate(LocalDate.now());
        event1.setTime(LocalTime.now());
        event1.setEventType("paid");
        event1.setOrganizer(organizer);
        event1.setTicketTiers(new ArrayList<>());
        event1.setEventVouchers(new ArrayList<>()); // Initialize eventVouchers list

        TicketTiers tier1 = new TicketTiers();
        tier1.setId(1L);
        tier1.setName("General Admission");
        tier1.setPrice(new BigDecimal("50.00"));
        tier1.setTotalSeats(100);
        tier1.setEvent(event1);
        event1.getTicketTiers().add(tier1);

        EventVouchers voucher1 = new EventVouchers();
        voucher1.setId(1L);
        voucher1.setCode("ROCK10");
        voucher1.setDiscountPercentage(new BigDecimal("10.00"));
        voucher1.setStartDate(LocalDate.now());
        voucher1.setEndDate(LocalDate.now().plusDays(7));
        voucher1.setEvent(event1);
        event1.getEventVouchers().add(voucher1);

        event2 = new Events();
        event2.setId(2L);
        event2.setName("Jazz Night");
        event2.setDescription("A smooth jazz night");
        event2.setCategory("Music");
        event2.setCity("New York");
        event2.setDate(LocalDate.now().plusDays(1));
        event2.setTime(LocalTime.now());
        event2.setEventType("paid");
        event2.setOrganizer(organizer);
        event2.setTicketTiers(new ArrayList<>());
        event2.setEventVouchers(new ArrayList<>()); // Initialize eventVouchers list

        TicketTiers tier2 = new TicketTiers();
        tier2.setId(2L);
        tier2.setName("VIP");
        tier2.setPrice(new BigDecimal("100.00"));
        tier2.setTotalSeats(50);
        tier2.setEvent(event2);
        event2.getTicketTiers().add(tier2);

        EventVouchers voucher2 = new EventVouchers();
        voucher2.setId(2L);
        voucher2.setCode("JAZZ15");
        voucher2.setDiscountPercentage(new BigDecimal("15.00"));
        voucher2.setStartDate(LocalDate.now());
        voucher2.setEndDate(LocalDate.now().plusDays(5));
        voucher2.setEvent(event2);
        event2.getEventVouchers().add(voucher2);
    }

    @Test
    void testSearchEvents() {
        // Arrange
        String category = "Music";
        String city = "New York";
        String dates = "this week";
        String prices = "paid";
        String keyword = "concert";
        Long organizerId = 1L;
        int page = 0;
        int size = 10;

        List<Events> eventsList = Arrays.asList(event1, event2);
        Page<Events> eventsPage = new PageImpl<>(eventsList, PageRequest.of(page, size), eventsList.size());

        when(eventRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(eventsPage);

        // Act
        PaginatedEventResponseDto result = eventService.searchEvents(category, city, dates, prices, keyword, organizerId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getEvents().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getPerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalEvents());

        // Verify event details
        assertTrue(result.getEvents().stream().anyMatch(e -> e.getName().equals("Rock Concert")));
        assertTrue(result.getEvents().stream().anyMatch(e -> e.getName().equals("Jazz Night")));

        // Verify organizer details
        result.getEvents().forEach(e -> {
            assertNotNull(e.getOrganizer());
            assertEquals("1", e.getOrganizer().getId());
            assertEquals("John", e.getOrganizer().getFirstName());
            assertEquals("Doe", e.getOrganizer().getLastName());
            assertEquals("john.doe@example.com", e.getOrganizer().getEmail());
        });

        // Verify ticket tiers
        result.getEvents().forEach(e -> {
            assertNotNull(e.getTicketTiers());
            assertFalse(e.getTicketTiers().isEmpty());
        });

        // Verify event vouchers
        result.getEvents().forEach(e -> {
            assertNotNull(e.getEventVouchers());
            assertFalse(e.getEventVouchers().isEmpty());
        });
    }

    @Test
    void testSearchEventsNoResults() {
        // Arrange
        String category = "Sports";
        String city = "Los Angeles";
        String dates = "this month";
        String prices = "free";
        String keyword = "basketball";
        Long organizerId = 2L;
        int page = 0;
        int size = 10;

        Page<Events> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);

        when(eventRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(emptyPage);

        // Act
        PaginatedEventResponseDto result = eventService.searchEvents(category, city, dates, prices, keyword, organizerId, page, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.getEvents().isEmpty());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getPerPage());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getTotalEvents());
    }
}
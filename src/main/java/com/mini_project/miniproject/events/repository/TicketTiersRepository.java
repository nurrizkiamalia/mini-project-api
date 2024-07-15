package com.mini_project.miniproject.events.repository;

import com.mini_project.miniproject.events.entity.TicketTiers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTiersRepository extends JpaRepository<TicketTiers, Long> {
//    @Query("SELECT SUM(tt.totalSeats) FROM TicketTiers tt JOIN Events e ON tt.event.id = e.id WHERE e.organizer.id = :organizerId")
//    Integer sumTotalSeatsByOrganizerId(@Param("organizerId") Long organizerId);
}

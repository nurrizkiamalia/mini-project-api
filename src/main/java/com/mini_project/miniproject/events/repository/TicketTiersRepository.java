package com.mini_project.miniproject.events.repository;

import com.mini_project.miniproject.events.entity.TicketTiers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTiersRepository extends JpaRepository<TicketTiers, Long> {
}

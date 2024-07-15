package com.mini_project.miniproject.events.repository;

import com.mini_project.miniproject.events.entity.Events;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Events, Long>, JpaSpecificationExecutor<Events> {
    Page<Events> findByOrganizerId(Long userId, Pageable pageable);
}

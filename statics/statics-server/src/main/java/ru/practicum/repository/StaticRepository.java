package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface StaticRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.createTime BETWEEN :start AND :end")
    List<Event> findAllByCreateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

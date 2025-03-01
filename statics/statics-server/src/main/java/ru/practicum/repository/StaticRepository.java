package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;

public interface StaticRepository extends JpaRepository<Event, Long> {
}

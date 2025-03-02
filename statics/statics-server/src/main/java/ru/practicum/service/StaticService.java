package ru.practicum.service;

import ru.practicum.NewEventRequest;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StaticService {
    void create(NewEventRequest newEventRequest);
    List<ViewStats> get(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris);
}

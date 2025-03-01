package ru.practicum.service;

import ru.practicum.EventDto;
import ru.practicum.NewEventRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface StaticService {
    void create(NewEventRequest newEventRequest);
    List<EventDto> get(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris);
}

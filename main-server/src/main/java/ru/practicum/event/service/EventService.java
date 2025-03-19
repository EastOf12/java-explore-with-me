package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventFullDtoWithViews;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventShortDtoWithViews;
import ru.practicum.event.request.NewEventRequest;
import ru.practicum.event.request.UpdateEventAdminRequest;
import ru.practicum.event.request.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventRequest newEventRequest);
    EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest updateEvent);
    EventFullDto getEventsByOwner(Long userId, Long eventId);
    List<EventShortDto> getEventsByOwner(Long userId, Integer from, Integer size);
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);
    List<EventFullDtoWithViews> getEventsByAdminParams(List<Long> users, List<String> states, List<Long> categories,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                       Integer from, Integer size);
    List<EventShortDtoWithViews> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                           Integer size, HttpServletRequest request) throws Exception;
    EventFullDtoWithViews getEventById(Long eventId, HttpServletRequest request) throws Exception;

}

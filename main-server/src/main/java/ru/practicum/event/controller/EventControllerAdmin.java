package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventFullDtoWithViews;
import ru.practicum.event.request.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    } //Обновить событие

    @GetMapping
    public List<EventFullDtoWithViews> getEventsByAdminParams(@RequestParam(required = false) List<Long> users,
                                                              @RequestParam(required = false) List<String> states,
                                                              @RequestParam(required = false) List<Long> categories,
                                                              @RequestParam(required = false) @DateTimeFormat(pattern =
                                                                      "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                              @RequestParam(required = false) @DateTimeFormat(pattern =
                                                                      "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                              @RequestParam(value = "from", defaultValue = "0")
                                                              @PositiveOrZero Integer from,
                                                              @RequestParam(value = "size", defaultValue = "10")
                                                              @Positive Integer size) {


        return eventService.getEventsByAdminParams(users, states, categories, rangeStart, rangeEnd, from, size);
    } //Получить события
}

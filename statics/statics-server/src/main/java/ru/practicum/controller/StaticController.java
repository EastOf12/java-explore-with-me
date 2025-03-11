package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.NewEventRequest;
import ru.practicum.ViewStats;
import ru.practicum.service.StaticService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class StaticController {
    private final StaticService staticService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody NewEventRequest newEventRequest) {
        staticService.create(newEventRequest);
    } //Создать событие

    @GetMapping(path = "/stats")
    public List<ViewStats> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                               @RequestParam(defaultValue = "false") Boolean unique,
                               @RequestParam(required = false) List<String> uris) {

        if (uris == null || uris.isEmpty()) {
            uris = new ArrayList<>();
        }

        return staticService.get(start, end, unique, uris);
    } //Передаем информацию по событиям
}

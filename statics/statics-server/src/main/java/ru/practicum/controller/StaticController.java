package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EventDto;
import ru.practicum.NewEventRequest;
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
    public List<EventDto> get(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end,
                              @RequestParam(defaultValue = "false") Boolean unique,
                              @RequestParam(required = false) List<String> uris) {

        if (uris == null || uris.isEmpty()) {
            uris = new ArrayList<>();
        }

        return staticService.get(start, end, unique, uris);
    } //Получаем события


}

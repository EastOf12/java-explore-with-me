package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EventDto;
import ru.practicum.NewEventRequest;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repository.StaticRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaticServiceImpl implements StaticService{
    private final StaticRepository staticRepository;

    @Transactional
    public void create(NewEventRequest newEventRequest) {
        log.info("Создаем новое событие");

        staticRepository.save(EventMapper.mapToEvent(newEventRequest));
    }

    @Override
    public List<EventDto> get(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        log.info("Передаем информацию по событиям с {} по {}", start, end);

        return null;
    }

}

package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.NewEventRequest;
import ru.practicum.ViewStats;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.StaticRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaticServiceImpl implements StaticService {
    private final StaticRepository staticRepository;

    @Transactional
    public void create(NewEventRequest newEventRequest) {
        log.info("Создаем новое событие");

        staticRepository.save(EventMapper.mapToEvent(newEventRequest));
    }

    @Override
    public List<ViewStats> get(
            LocalDateTime start,
            LocalDateTime end,
            Boolean unique, List<String> uris) {

        List<ViewStats> viewStats;

        if (unique && !uris.isEmpty()) {
            log.info("Передаем информацию по уникальным событиям с {} по {} по этим uri {}", start, end, uris);
            viewStats = staticRepository.findUniqueAppUriCountByCreateTimeBetweenAndUris(start, end, uris);
        } else if (!uris.isEmpty()) {
            log.info("Передаем информацию событиям с {} по {} по этим uri {}", start, end, uris);
            viewStats = staticRepository.findAppUriCountByCreateTimeBetweenAndUris(start, end, uris);
        } else if (unique) {
            log.info("Передаем информацию по уникальным событиям с {} по {}", start, end);
            viewStats = staticRepository.findUniqueAppUriCountByCreateTimeBetween(start, end);
        } else {
            log.info("Передаем информацию по событиям с {} по {}", start, end);
            viewStats = staticRepository.findAppUriCountByCreateTimeBetween(start, end);
        }

        return viewStats;
    }
}

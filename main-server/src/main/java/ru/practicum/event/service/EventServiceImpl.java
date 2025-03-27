package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.NewEventRequest;
import ru.practicum.StaticClient;
import ru.practicum.ViewStats;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventFullDtoWithViews;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventShortDtoWithViews;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateActionAdmin;
import ru.practicum.event.model.StateActionPrivate;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.request.UpdateEventAdminRequest;
import ru.practicum.event.request.UpdateEventUserRequest;
import ru.practicum.exeption.ForbiddenException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.exeption.ValidationException;
import ru.practicum.locations.Location;
import ru.practicum.locations.LocationMapper;
import ru.practicum.locations.LocationRepository;
import ru.practicum.request.dto.ConfirmedRequests;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.model.State.PENDING;
import static ru.practicum.event.model.State.PUBLISHED;
import static ru.practicum.event.model.StateActionAdmin.PUBLISH_EVENT;
import static ru.practicum.event.model.StateActionAdmin.REJECT_EVENT;
import static ru.practicum.event.model.StateActionPrivate.CANCEL_REVIEW;
import static ru.practicum.event.model.StateActionPrivate.SEND_TO_REVIEW;
import static ru.practicum.request.model.RequestStatus.CONFIRMED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    final EventRepository eventRepository;
    final LocationRepository locationRepository;
    final RequestRepository requestRepository;
    final CommentRepository commentRepository;
    final StaticClient staticClient;

    @Value("${app}")
    String app;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, ru.practicum.event.request.NewEventRequest newEventRequest) {
        log.info("Запрос на новое событие");

        checkActualTime(newEventRequest.getEventDate());

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с " + userId + " не найден"));

        Long categoryId = newEventRequest.getCategory();

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Категория с id " + categoryId + " не найдена"));

        Location location = getOrCreateLocation(LocationMapper.mapToLocation(newEventRequest.getLocation()));

        Event event = EventMapper.matToEvent(newEventRequest, user, category, location, PENDING);

        return EventMapper.mapToEventFullDto(eventRepository.save(event), 0L);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        log.info("Запрос на обновления события");

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));

        if (event.getState() == PUBLISHED) {
            throw new ForbiddenException("Нельзя обновить событие которое уже опубликовано");
        }

        String annotation = updateEvent.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }

        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория с id " + updateEvent.getCategory() + " не найдена"));

            event.setCategory(category);
        }

        String description = updateEvent.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }

        LocalDateTime eventDate = updateEvent.getEventDate();
        if (eventDate != null) {
            checkActualTime(eventDate);
            event.setEventDate(eventDate);
        }

        if (updateEvent.getLocation() != null) {
            Location location = getOrCreateLocation(LocationMapper.mapToLocation(updateEvent.getLocation()));
            event.setLocation(location);
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }

        String title = updateEvent.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }

        if (updateEvent.getStateAction() != null) {
            StateActionPrivate stateActionPrivate = StateActionPrivate.valueOf(updateEvent.getStateAction());
            if (stateActionPrivate.equals(SEND_TO_REVIEW)) {
                event.setState(PENDING);
            } else if (stateActionPrivate.equals(CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }
        }

        return EventMapper.mapToEventFullDto(eventRepository.save(event),
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Override
    public EventFullDto getEventsByOwner(Long userId, Long eventId) {
        log.info("Запрос на получение события");

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));

        return EventMapper.mapToEventFullDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Override
    public List<EventShortDto> getEventsByOwner(Long userId, Integer from, Integer size) {
        log.info("Запрос на получение событий");

        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
        return events.stream()
                .map(event -> EventMapper.mapToEventShortDto(event, confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        log.info("Запрос на обновление события");

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));

        if (updateEvent.getStateAction() != null) {
            StateActionAdmin stateAction = StateActionAdmin.valueOf(updateEvent.getStateAction());
            if (!event.getState().equals(PENDING) && stateAction.equals(PUBLISH_EVENT)) {
                throw new ForbiddenException("Событие должно быть в PENDING");
            }
            if (event.getState().equals(PUBLISHED) && stateAction.equals(REJECT_EVENT)) {
                throw new ForbiddenException("Событие уже опубликовано");
            }
            if (stateAction.equals(PUBLISH_EVENT)) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
        String annotation = updateEvent.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория с id " + updateEvent.getCategory() + " не найдена"));

            event.setCategory(category);
        }
        String description = updateEvent.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = updateEvent.getEventDate();
        if (eventDate != null) {
            checkActualTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(getOrCreateLocation(LocationMapper.mapToLocation(updateEvent.getLocation())));
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        String title = updateEvent.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        return EventMapper.mapToEventFullDto(eventRepository.save(event),
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    public List<EventFullDtoWithViews> getEventsByAdminParams(List<Long> users, List<String> states, List<Long> categories,
                                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                              Integer from, Integer size) {
        log.info("Запрос на получение полной информации по событиям");

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Некорректный запрос");
        }
        Specification<Event> specification = Specification.where(null);
        if (users != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (states != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        List<Event> events = eventRepository.findAll(specification, PageRequest.of(from / size, size)).getContent();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        return getFullEventsDetails(events);
    }

    @Override
    public List<EventShortDtoWithViews> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                                  Integer size, HttpServletRequest request) throws Exception {
        log.info("Запрос на получение краткой информации по событиям");

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Не верный диапазон поиска");
        }
        Specification<Event> specification = Specification.where(null);
        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (paid != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("paid"), paid));
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), PUBLISHED));
        PageRequest pageRequest = switch (sort) {
            case "EVENT_DATE" -> PageRequest.of(from / size, size, Sort.by("eventDate"));
            case "VIEWS" -> PageRequest.of(from / size, size, Sort.by("views").descending());
            default -> throw new ValidationException("Неправильный sort: " + sort);
        };
        List<Event> events = eventRepository.findAll(specification, pageRequest).getContent();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        NewEventRequest newEventRequest = new NewEventRequest(app, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());

        staticClient.createEvent(newEventRequest);

        return getShortEventsDetails(events);

    }

    @Override
    public EventFullDtoWithViews getEventById(Long eventId, HttpServletRequest request) throws Exception {
        log.info("Запрос на получение полной информации по событию");

        NewEventRequest newEventRequest = new NewEventRequest(app, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());

        staticClient.createEvent(newEventRequest);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));

        if (event.getState() != PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        List<ViewStats> response = staticClient.getStats(event.getCreatedOn().minusSeconds(1), LocalDateTime.now(),
                true, List.of(request.getRequestURI()));

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> statsDto = mapper.convertValue(response, new TypeReference<>() {
        });

        Long comments = (long) commentRepository.findAllByEventId(eventId).size();

        EventFullDtoWithViews result;
        if (!statsDto.isEmpty()) {
            result = EventMapper.mapToEventFullDtoWithViews(event, statsDto.getFirst().getHits(),
                    requestRepository.countByEventIdAndStatus(eventId, CONFIRMED), comments);
        } else {
            result = EventMapper.mapToEventFullDtoWithViews(event, 0L,
                    requestRepository.countByEventIdAndStatus(eventId, CONFIRMED), comments);
        }

        return result;
    }

    private void checkActualTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Время до начала события не может быть меньше 2х часов");
        }
    }

    private Location getOrCreateLocation(Location location) {
        if (locationRepository.existsByLatAndLon(location.getLat(), location.getLon())) {
            return locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        } else {
            return locationRepository.save(location);
        }
    }

    private List<EventShortDtoWithViews> getShortEventsDetails(List<Event> events) {
        List<EventShortDtoWithViews> result = new ArrayList<>();

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        Optional<LocalDateTime> start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo);
        List<ViewStats> response = staticClient.getStats(start.get(), LocalDateTime.now(), true, uris);
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
        for (Event event : events) {
            ObjectMapper mapper = new ObjectMapper();
            List<ViewStats> statsDto = mapper.convertValue(response, new TypeReference<>() {
            });
            if (!statsDto.isEmpty()) {
                result.add(EventMapper.mapToEventShortDtoWithViews(event, statsDto.getFirst().getHits(),
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            } else {
                result.add(EventMapper.mapToEventShortDtoWithViews(event, 0L,
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            }
        }

        return result;
    }

    private List<EventFullDtoWithViews> getFullEventsDetails(List<Event> events) {
        List<EventFullDtoWithViews> result = new ArrayList<>();

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        Optional<LocalDateTime> start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo);
        List<ViewStats> response = staticClient.getStats(start.get(), LocalDateTime.now(), true, uris);
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED).stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));


        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByEventIdIn(eventIds);

        Map<Long, List<Comment>> eventComments = new HashMap<>();

        for (Comment comment: comments) {
            Long eventId = comment.getEvent().getId();

            if (eventComments.containsKey(eventId)) {
                eventComments.get(eventId).add(comment);
            } else {
                eventComments.put(eventId, new ArrayList<>(List.of(comment)));
            }
        }

        for (Event event : events) {
            ObjectMapper mapper = new ObjectMapper();
            List<ViewStats> statsDto = mapper.convertValue(response, new TypeReference<>() {
            });

            long commentsCount = 0L;

            if (eventComments.containsKey(event.getId())) {
                commentsCount = eventComments.get(event.getId()).size();
            }

            if (!statsDto.isEmpty()) {
                result.add(EventMapper.mapToEventFullDtoWithViews(event, statsDto.getFirst().getHits(),
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        commentsCount));
            } else {
                result.add(EventMapper.mapToEventFullDtoWithViews(event, 0L,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        commentsCount));
            }
        }

        return result;
    }
}

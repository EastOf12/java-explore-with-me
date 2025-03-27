package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventFullDtoWithViews;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventShortDtoWithViews;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.request.NewEventRequest;
import ru.practicum.locations.Location;
import ru.practicum.locations.LocationMapper;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;


@UtilityClass
public class EventMapper {
    public Event matToEvent(NewEventRequest newEventRequest) {
        return Event.builder()
                .annotation(newEventRequest.getAnnotation())
                .description(newEventRequest.getDescription())
                .eventDate(newEventRequest.getEventDate())
                .location(LocationMapper.mapToLocation(newEventRequest.getLocation()))
                .paid(newEventRequest.getPaid())
                .participantLimit(newEventRequest.getParticipantLimit())
                .requestModeration(newEventRequest.getRequestModeration())
                .title(newEventRequest.getTitle())
                .build();
    }

    public Event matToEvent(NewEventRequest newEventRequest, User user, Category category, Location location, State state) {
        return Event.builder()
                .annotation(newEventRequest.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventRequest.getDescription())
                .eventDate(newEventRequest.getEventDate())
                .initiator(user)
                .location(location)
                .paid(newEventRequest.getPaid())
                .participantLimit(newEventRequest.getParticipantLimit())
                .requestModeration(newEventRequest.getRequestModeration())
                .state(state)
                .title(newEventRequest.getTitle())
                .build();
    }

    public EventFullDto mapToEventFullDto(Event event, Long confirmedRequests) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto mapToEventShortDto(Event event, Long confirmedRequests) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public EventFullDtoWithViews mapToEventFullDtoWithViews(Event event,
                                                            Long views,
                                                            Long confirmedRequests,
                                                            Long comments) {
        return EventFullDtoWithViews.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .comments(comments)
                .build();
    }

    public EventShortDtoWithViews mapToEventShortDtoWithViews(Event event, Long views, Long confirmedRequests) {
        return EventShortDtoWithViews.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

}

package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.EventDto;
import ru.practicum.NewEventRequest;
import ru.practicum.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static Event mapToEvent(NewEventRequest newEventRequest) {
        return new Event(
                newEventRequest.getApp(),
                newEventRequest.getUri(),
                newEventRequest.getIp(),
                newEventRequest.getTimestamp()
        );
    }

    public static EventDto mapToEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getApp(),
                event.getUri(),
                event.getIp(),
                event.getCreateTime()
        );
    }
}

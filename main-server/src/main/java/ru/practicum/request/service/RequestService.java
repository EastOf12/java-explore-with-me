package ru.practicum.request.service;


import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.request.EventRequestStatusUpdateRequest;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest statusUpdateRequest);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getRequestsByEventOwner(Long userId, Long eventId);

    List<RequestDto> getRequestsByUser(Long userId);
}

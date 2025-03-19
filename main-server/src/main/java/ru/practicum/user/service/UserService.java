package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.request.NewUserRequest;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);
    List<UserDto> get(List<Long> ids, Integer from, Integer size);
    void delete(Long id);
}

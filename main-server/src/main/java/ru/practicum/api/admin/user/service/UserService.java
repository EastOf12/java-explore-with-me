package ru.practicum.api.admin.user.service;

import ru.practicum.api.admin.user.dto.UserDto;
import ru.practicum.api.admin.user.request.NewUserRequest;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);

    List<UserDto> get(List<Long> ids);

    void delete(Long id);
}

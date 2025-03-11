package ru.practicum.api.admin.user.service;

import ru.practicum.api.admin.user.dto.UserDto;
import ru.practicum.api.admin.user.request.NewUserRequest;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);

    UserDto get(Long id);

    void delete(Long id);
}

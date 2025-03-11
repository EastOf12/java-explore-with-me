package ru.practicum.api.admin.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.admin.user.dto.UserDto;
import ru.practicum.api.admin.user.request.NewUserRequest;
import ru.practicum.api.admin.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody NewUserRequest newUserRequest) {
        return userService.create(newUserRequest);
    } //Создать пользователя

    @GetMapping
    public UserDto get(@RequestParam Long ids) {
        return userService.get(ids);
    } //Получить пользователя

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    } //Удалить пользователя
}

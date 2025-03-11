package ru.practicum.api.admin.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.admin.user.dto.UserDto;
import ru.practicum.api.admin.user.request.NewUserRequest;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        System.out.println("Вызвали create " + newUserRequest);
        return null;
    }

    @Override
    public UserDto get(Long id) {
        System.out.println("Вызвали get " + id);
        return null;
    }

    @Override
    public void delete(Long id) {
        System.out.println("Вызвали delete " + id);
    }
}

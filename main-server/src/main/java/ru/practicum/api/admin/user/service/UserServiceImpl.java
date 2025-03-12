package ru.practicum.api.admin.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.admin.user.dto.UserDto;
import ru.practicum.api.admin.user.exeption.EmailAlreadyExistsException;
import ru.practicum.api.admin.user.mapper.UserMapper;
import ru.practicum.api.admin.user.model.User;
import ru.practicum.api.admin.user.repository.UserRepository;
import ru.practicum.api.admin.user.request.NewUserRequest;
import ru.practicum.api.exeption.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        log.info("Создаем нового пользователя");

        if (!userRepository.findByEmailContainingIgnoreCase(newUserRequest.getEmail()).isEmpty()) {
            throw new EmailAlreadyExistsException("Email уже используется");
        }

        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(newUserRequest)));
    }

    @Override
    public List<UserDto> get(List<Long> ids) {

        log.info("Отдаем пользователей с ids {}", ids);

        List<User> users = userRepository.findAllById(ids);

        if(users.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserDto> userDtos = new ArrayList<>();

        for (User user: users) {
            userDtos.add(UserMapper.mapToUserDto(user));
        }

        return userDtos;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаляем пользователя с id {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        userRepository.delete(user);
    }
}

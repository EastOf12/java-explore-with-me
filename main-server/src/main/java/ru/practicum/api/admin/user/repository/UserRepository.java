package ru.practicum.api.admin.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.api.admin.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmailContainingIgnoreCase(String emailSearch);
    List<User> findAllById(Iterable<Long> ids);
}
package ru.practicum.api.admin.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.api.admin.category.model.Category;
import ru.practicum.api.admin.user.model.User;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String name);
}
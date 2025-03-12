package ru.practicum.api.admin.category.service;

import ru.practicum.api.admin.category.dto.CategoryDto;
import ru.practicum.api.admin.category.request.NewCategoryRequest;
import ru.practicum.api.admin.category.request.UpdateCategoryRequest;

public interface CategoryService {
    CategoryDto create(NewCategoryRequest newCategoryRequest);
    CategoryDto update(Long id, UpdateCategoryRequest updateCategoryRequest);
    void delete(Long id);
}

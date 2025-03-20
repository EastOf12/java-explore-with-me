package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.request.NewCategoryRequest;
import ru.practicum.category.request.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryRequest newCategoryRequest);

    CategoryDto update(Long id, UpdateCategoryRequest updateCategoryRequest);

    void delete(Long id);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);
}

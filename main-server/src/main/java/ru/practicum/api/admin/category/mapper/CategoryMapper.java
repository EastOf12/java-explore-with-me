package ru.practicum.api.admin.category.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.api.admin.category.dto.CategoryDto;
import ru.practicum.api.admin.category.model.Category;
import ru.practicum.api.admin.category.request.NewCategoryRequest;
import ru.practicum.api.admin.category.request.UpdateCategoryRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

    public static Category mapToCategory(NewCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return category;
    }

    public static Category mapToCategory(Long id, UpdateCategoryRequest request) {
        Category category = new Category();
        category.setId(id);
        category.setName(request.getName());
        return category;
    }

    public static CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
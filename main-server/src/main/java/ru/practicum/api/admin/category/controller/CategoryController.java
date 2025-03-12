package ru.practicum.api.admin.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.admin.category.dto.CategoryDto;
import ru.practicum.api.admin.category.request.NewCategoryRequest;
import ru.practicum.api.admin.category.request.UpdateCategoryRequest;
import ru.practicum.api.admin.category.service.CategoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody NewCategoryRequest newCategoryRequest) {
        return categoryService.create(newCategoryRequest);
    } //Создать категорию

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId, @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        return categoryService.update(catId, updateCategoryRequest);
    } //Обновить категорию

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    } //Удалить категорию
}

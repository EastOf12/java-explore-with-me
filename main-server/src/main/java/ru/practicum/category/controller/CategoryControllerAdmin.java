package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.request.NewCategoryRequest;
import ru.practicum.category.request.UpdateCategoryRequest;
import ru.practicum.category.service.CategoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/categories")
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryRequest newCategoryRequest) {
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

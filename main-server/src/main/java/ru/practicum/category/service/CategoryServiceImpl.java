package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.request.NewCategoryRequest;
import ru.practicum.category.request.UpdateCategoryRequest;
import ru.practicum.exeption.ForbiddenException;
import ru.practicum.exeption.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryRequest newCategoryRequest) {
        log.info("Создаем новую категорию");

        if (!categoryRepository.findByNameContainingIgnoreCase(newCategoryRequest.getName()).isEmpty()) {
            throw new ForbiddenException("Категория " + newCategoryRequest.getName() + " уже используется");
        }

        return CategoryMapper.mapToCategoryDto(categoryRepository.
                save(CategoryMapper.mapToCategory(newCategoryRequest)));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, UpdateCategoryRequest updateCategoryRequest) {
        log.info("Обновляем категорию {}", id);

        //Проверяем, что нет других категорий с таким названием
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(updateCategoryRequest.getName());

        for (Category category : categories) {
            if (!category.getId().equals(id) && category.getName().equals(updateCategoryRequest.getName())) {
                throw new ForbiddenException("Категория " + updateCategoryRequest.getName() +
                        " уже используется категорией с id " + category.getId());
            }
        }

        //Обновляем название категории
        return CategoryMapper.mapToCategoryDto(categoryRepository.
                save(CategoryMapper.mapToCategory(id, updateCategoryRequest)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаляем категорию {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id " + id + " не найдена"));

        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Category with id=" + categoryId + " was not found"));

        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryMapper::mapToCategoryDto).collect(Collectors.toList());
    }

}

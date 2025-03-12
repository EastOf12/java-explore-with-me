package ru.practicum.api.admin.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.admin.category.dto.CategoryDto;
import ru.practicum.api.admin.category.exeption.CategoryAlreadyExistsException;
import ru.practicum.api.admin.category.mapper.CategoryMapper;
import ru.practicum.api.admin.category.model.Category;
import ru.practicum.api.admin.category.repository.CategoryRepository;
import ru.practicum.api.admin.category.request.NewCategoryRequest;
import ru.practicum.api.admin.category.request.UpdateCategoryRequest;
import ru.practicum.api.exeption.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryRequest newCategoryRequest) {
        log.info("Создаем новую категорию");

        if (!categoryRepository.findByNameContainingIgnoreCase(newCategoryRequest.getName()).isEmpty()) {
            throw new CategoryAlreadyExistsException("Категория " + newCategoryRequest.getName() + " уже используется");
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

        for(Category category: categories) {
            if(!category.getId().equals(id) && category.getName().equals(updateCategoryRequest.getName())) {
                throw new CategoryAlreadyExistsException("Категория " + updateCategoryRequest.getName() +
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
}

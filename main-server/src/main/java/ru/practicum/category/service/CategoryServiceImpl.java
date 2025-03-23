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
import ru.practicum.category.request.CategoryRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
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
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto create(CategoryRequest categoryRequest) {
        log.info("Создаем новую категорию");

        if (!categoryRepository.findByNameContainingIgnoreCase(categoryRequest.getName()).isEmpty()) {
            throw new ForbiddenException("Категория " + categoryRequest.getName() + " уже используется");
        }

        return CategoryMapper.mapToCategoryDto(categoryRepository
                .save(CategoryMapper.mapToCategory(categoryRequest)));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryRequest categoryRequest) {
        log.info("Обновляем категорию {}", id);

        //Проверяем, что нет других категорий с таким названием
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(categoryRequest.getName());

        for (Category category : categories) {
            if (!category.getId().equals(id) && category.getName().equals(categoryRequest.getName())) {
                throw new ForbiddenException("Категория " + categoryRequest.getName() +
                        " уже используется категорией с id " + category.getId());
            }
        }

        //Обновляем название категории
        return CategoryMapper.mapToCategoryDto(categoryRepository
                .save(CategoryMapper.mapToCategory(id, categoryRequest)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаляем категорию {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id " + id + " не найдена"));

        List<Event> events = eventRepository.findAllByCategoryId(id);

        if (!events.isEmpty()) {
            throw new ForbiddenException("The category is not empty");
        }

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

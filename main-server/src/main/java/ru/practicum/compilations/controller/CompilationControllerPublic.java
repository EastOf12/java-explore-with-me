package ru.practicum.compilations.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationControllerPublic {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    } //Получить подборки

    @GetMapping("/{compilationId}")
    public CompilationDto getCompilationById(@PathVariable Long compilationId) {
        return compilationService.getCompilationById(compilationId);
    } //Получить подборку по id
}

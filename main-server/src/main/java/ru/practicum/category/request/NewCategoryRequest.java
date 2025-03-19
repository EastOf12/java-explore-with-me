package ru.practicum.category.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class NewCategoryRequest {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
package ru.practicum.api.admin.user.request;

import lombok.Data;

@Data
public class NewUserRequest {
    private String name;
    private String email;
}
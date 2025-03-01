package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime createTime;
}

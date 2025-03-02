package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private long hits;

    public ViewStats(String app, String uri) {
        this.app = app;
        this.uri = uri;
    }
}

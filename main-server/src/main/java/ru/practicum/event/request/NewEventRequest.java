package ru.practicum.event.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.locations.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventRequest {

    @Size(min = 20, max = 2000)
    @NotBlank
    String annotation;

    @NotNull
    Long category;

    @Size(min = 20, max = 7000)
    @NotBlank
    String description;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    @Valid
    LocationDto location;

    boolean paid = false;

    @PositiveOrZero
    int participantLimit = 0;

    boolean requestModeration = true;

    @Size(min = 3, max = 120)
    @NotBlank
    String title;

    public Boolean getPaid() {
        return paid;
    }

    public Boolean getRequestModeration() {
        return requestModeration;
    }
}


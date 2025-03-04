package ru.practicum;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StaticClient {
    private final RestTemplate restTemplate;

    private final String baseUrl = "http://localhost:9090";

    public void createEvent(NewEventRequest newEventRequest) throws Exception {
        String url = baseUrl + "/hit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NewEventRequest> requestEntity = new HttpEntity<>(newEventRequest, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new Exception("Код ответа сервиса не 201");
        }
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        String url = String.format("%s/stats?start=%s&end=%s&unique=%s",
                baseUrl, start, end, unique);

        if (uris != null && !uris.isEmpty()) {
            String uriParams = String.join(",", uris);
            url += "&uris=" + uriParams;
        }

        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ViewStats>>() {
                });

        return response.getBody();
    }
}
package com.demo.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenedResponse {
    @Schema( description = "Already shortened url's short code",
            example = "abc123")
    private String shortUrl;
}

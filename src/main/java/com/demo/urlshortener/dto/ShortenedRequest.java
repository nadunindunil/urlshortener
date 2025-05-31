package com.demo.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenedRequest {
    @Schema( description = "Url needs to be shortened",
            example = "https://www.x.com/path",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Url cannot be blank")
    @Pattern( regexp = "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "Url must be in a valid url format")
    private String originalUrl;
}

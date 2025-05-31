package com.demo.urlshortener.controller;

import com.demo.urlshortener.dto.ShortenedInfo;
import com.demo.urlshortener.dto.ShortenedRequest;
import com.demo.urlshortener.dto.ShortenedResponse;
import com.demo.urlshortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Validated
@Tag(name = "URLs", description = "Endpoint for url shortening and redirection.")
public class UrlController {
    private final UrlShortenerService urlShortenerService;

    @Autowired
    public UrlController( UrlShortenerService urlShortenerService ) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/v1/urls/shorten")
    public ResponseEntity<?> shortenURL(@Valid @RequestBody ShortenedRequest request) {
        String shortUrl = urlShortenerService.shortenUrl(request.getOriginalUrl());
        return ResponseEntity.ok( new ShortenedResponse(shortUrl));
    }

    @GetMapping("/v1/urls/visit/{shortCode}")
    public void redirectToOriginalUrl(
            @NotBlank
            @NotNull
            @Size(min = 6, max = 6, message = "Short code must be 6 chars.")
            @PathVariable("shortCode")
            String shortCode,
            HttpServletResponse response) throws IOException {
        String originalUrl = urlShortenerService.getOriginalUrl(shortCode);

        if ( originalUrl != null ) {
            response.sendRedirect(originalUrl);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/v1/urls/info/{shortCode}")
    public ResponseEntity<ShortenedInfo> getUrlInfo(
                @NotBlank
                @NotNull
                @PathVariable("shortCode")
                @Size(min = 6, max = 6, message = "Short code must be 6 chars.")
                String shortCode) {
        String shortenedUrl = urlShortenerService.getOriginalUrl(shortCode);
        return ResponseEntity.ok(new ShortenedInfo( shortCode, shortenedUrl ));
    }
}

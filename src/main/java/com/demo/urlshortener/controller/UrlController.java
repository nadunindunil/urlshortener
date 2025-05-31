package com.demo.urlshortener.controller;

import com.demo.urlshortener.dto.ShortenedInfo;
import com.demo.urlshortener.dto.ShortenedRequest;
import com.demo.urlshortener.dto.ShortenedResponse;
import com.demo.urlshortener.service.UrlShortenerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/v1/urls")
public class UrlController {
    private final UrlShortenerService urlShortenerService;

    @Autowired
    public UrlController( UrlShortenerService urlShortenerService ) {
        this.urlShortenerService = urlShortenerService;
    }

    @Operation(summary = "Shorten a URL", description = "Creates a shortened URL from the provided original URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL shortened successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid URL format")
    })
    @PostMapping("/shorten")
    public ResponseEntity<?> shortenURL(@Valid @RequestBody ShortenedRequest request) {
        String shortUrl = urlShortenerService.shortenUrl(request.getOriginalUrl());
        return ResponseEntity.ok( new ShortenedResponse(shortUrl));
    }

    @Operation(summary = "Redirect to original URL", description = "Redirects to the original URL using the short code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
        @ApiResponse(responseCode = "404", description = "Short code not found"),
        @ApiResponse(responseCode = "400", description = "Invalid short code format")
    })
    @GetMapping("/visit/{shortCode}")
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

    @Operation(summary = "Get URL information", description = "Returns information about the shortened URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Short code not found"),
        @ApiResponse(responseCode = "400", description = "Invalid short code format")
    })
    @GetMapping("/info/{shortCode}")
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

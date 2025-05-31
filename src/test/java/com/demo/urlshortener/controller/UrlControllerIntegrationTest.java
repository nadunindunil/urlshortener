package com.demo.urlshortener.controller;

import com.demo.urlshortener.dto.ShortenedRequest;
import com.demo.urlshortener.entity.UrlMapping;
import com.demo.urlshortener.repository.UrlMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UrlControllerIntegrationTest {
    private static final String VALID_URL = "https://www.example.com";
    private static final String VALID_SHORT_CODE = "abc123";
    private static final String INVALID_SHORT_CODE = "abc12"; // 5 chars
    private static final String INVALID_URL = "not-a-url";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @BeforeEach
    void setUp() {
        urlMappingRepository.deleteAll();
    }

    @Test
    void shortenUrl_ValidUrl_ReturnsShortUrl() throws Exception {
        ShortenedRequest request = new ShortenedRequest(VALID_URL);

        mockMvc.perform(post("/v1/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").exists());
    }

    @Test
    void shortenUrl_InvalidUrl_ReturnsBadRequest() throws Exception {
        ShortenedRequest request = new ShortenedRequest(INVALID_URL);

        mockMvc.perform(post("/v1/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shortenUrl_EmptyUrl_ReturnsBadRequest() throws Exception {
        ShortenedRequest request = new ShortenedRequest("");

        mockMvc.perform(post("/v1/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void visitUrl_ValidShortCode_RedirectsToOriginalUrl() throws Exception {
        // First create a shortened URL
        UrlMapping mapping = new UrlMapping(VALID_SHORT_CODE, VALID_URL);
        urlMappingRepository.save(mapping);

        mockMvc.perform(get("/v1/urls/visit/{shortCode}", VALID_SHORT_CODE))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", VALID_URL));
    }

    @Test
    void visitUrl_InvalidShortCode_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/urls/visit/{shortCode}", INVALID_SHORT_CODE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Short code must be 6 chars")));
    }

    @Test
    void visitUrl_NonexistentShortCode_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/v1/urls/visit/{shortCode}", "nonexs"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUrlInfo_ValidShortCode_ReturnsInfo() throws Exception {
        // First create a shortened URL
        UrlMapping mapping = new UrlMapping(VALID_SHORT_CODE, VALID_URL);
        urlMappingRepository.save(mapping);

        mockMvc.perform(get("/v1/urls/info/{shortCode}", VALID_SHORT_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortcode").value(VALID_SHORT_CODE))
                .andExpect(jsonPath("$.url").value(VALID_URL));
    }

    @Test
    void getUrlInfo_InvalidShortCode_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/urls/info/{shortCode}", INVALID_SHORT_CODE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Short code must be 6 chars")));
    }

    @Test
    void getUrlInfo_NonexistentShortCode_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/v1/urls/info/{shortCode}", "nonexs"))
                .andExpect(status().isNotFound());
    }
}

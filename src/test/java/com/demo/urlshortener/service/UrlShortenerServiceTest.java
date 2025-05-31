package com.demo.urlshortener.service;

import com.demo.urlshortener.entity.UrlMapping;
import com.demo.urlshortener.exception.ResourceNotFoundException;
import com.demo.urlshortener.exception.ShortCodeCollisionException;
import com.demo.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    private static final String BASE_URL = "http://short.url/";
    private static final String VALID_URL = "http://example.com";
    private static final String INVALID_URL = "not-a-url";
    private static final String SAMPLE_SHORT_CODE = "abc123";

    @Mock
    private UrlMappingRepository urlMappingRepository;

    private UrlShortenerService urlShortenerService;

    @BeforeEach
    void setUp() {
        urlShortenerService = new UrlShortenerService(urlMappingRepository, BASE_URL);
    }

    @Test
    void shortenUrl_ValidUrl_ReturnsShortenedUrl() {
        when(urlMappingRepository.findByOriginalUrl(VALID_URL)).thenReturn(Optional.empty());
        when(urlMappingRepository.existsByShortCode(any())).thenReturn(false);
        when(urlMappingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        String shortened = urlShortenerService.shortenUrl(VALID_URL);

        assertNotNull(shortened);
        assertTrue(shortened.startsWith(BASE_URL));
        verify(urlMappingRepository).save(any(UrlMapping.class));
    }

    @Test
    void shortenUrl_InvalidUrl_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.shortenUrl(INVALID_URL)
        );
    }

    @Test
    void shortenUrl_ExistingUrl_ReturnsCachedShortUrl() {
        UrlMapping existingMapping = new UrlMapping(SAMPLE_SHORT_CODE, VALID_URL);
        when(urlMappingRepository.findByOriginalUrl(VALID_URL))
                .thenReturn(Optional.of(existingMapping));

        String shortened = urlShortenerService.shortenUrl(VALID_URL);

        assertEquals(BASE_URL + SAMPLE_SHORT_CODE, shortened);
        verify(urlMappingRepository, never()).save(any());
    }

    @Test
    void getOriginalUrl_ValidShortCode_ReturnsOriginalUrl() {
        UrlMapping mapping = new UrlMapping(SAMPLE_SHORT_CODE, VALID_URL);
        when(urlMappingRepository.findByShortCode(SAMPLE_SHORT_CODE))
                .thenReturn(Optional.of(mapping));

        String originalUrl = urlShortenerService.getOriginalUrl(SAMPLE_SHORT_CODE);

        assertEquals(VALID_URL, originalUrl);
    }

    @Test
    void getOriginalUrl_InvalidShortCode_ThrowsException() {
        when(urlMappingRepository.findByShortCode(SAMPLE_SHORT_CODE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                urlShortenerService.getOriginalUrl(SAMPLE_SHORT_CODE)
        );
    }

    @Test
    void shortenUrl_CollisionHandling_GeneratesNewCode() {
        when(urlMappingRepository.findByOriginalUrl(VALID_URL)).thenReturn(Optional.empty());
        when(urlMappingRepository.existsByShortCode(any()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(urlMappingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        String shortened = urlShortenerService.shortenUrl(VALID_URL);

        assertNotNull(shortened);
        assertTrue(shortened.startsWith(BASE_URL));
        verify(urlMappingRepository, times(3)).existsByShortCode(any());
    }

    @Test
    void whenCollision_ShouldRetryAndEventuallySucceed() {
        when(urlMappingRepository.findByOriginalUrl(VALID_URL))
                .thenReturn(Optional.empty());

        when(urlMappingRepository.existsByShortCode(anyString()))
                .thenReturn(true, true, true, false);

        String result = urlShortenerService.shortenUrl(VALID_URL);

        assertTrue(result.startsWith(BASE_URL));
        verify(urlMappingRepository, times(4)).existsByShortCode(anyString());
        verify(urlMappingRepository).save(any(UrlMapping.class));
    }

    @Test
    void whenMaxAttemptsExceeded_ShouldThrowException() {
        when(urlMappingRepository.findByOriginalUrl(VALID_URL))
                .thenReturn(Optional.empty());
        when(urlMappingRepository.existsByShortCode(anyString()))
                .thenReturn(true);

        assertThrows(ShortCodeCollisionException.class,
                () -> urlShortenerService.shortenUrl(VALID_URL));

        verify(urlMappingRepository, times(10)).existsByShortCode(anyString());
        verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    }
}

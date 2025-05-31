package com.demo.urlshortener.service;

import com.demo.urlshortener.entity.UrlMapping;
import com.demo.urlshortener.exception.ResourceNotFoundException;
import com.demo.urlshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class UrlShortenerService {
    private static final int SHORT_CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();
    private final String baseUrl;
    private final UrlMappingRepository urlMappingRepository;

    @Autowired
    public UrlShortenerService( UrlMappingRepository urlMappingRepository, @Value("${app.baseUrl}") String baseUrl) {
        this.urlMappingRepository = urlMappingRepository;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public String shortenUrl(String url) {
        if (!isValidUrl(url) ) {
            throw new IllegalArgumentException("Invalid URL format provided.");
        }

        Optional<UrlMapping> existingUrlMap = urlMappingRepository.findByOriginalUrl(url);

        if ( existingUrlMap.isPresent() ) {
            return baseUrl + existingUrlMap.get().getShortCode();
        }

        String shortCode;
        do {
            shortCode = generateShortCode();
        } while ( urlMappingRepository.existsByShortCode(shortCode) );

        urlMappingRepository.save( new UrlMapping(shortCode, url));

        return baseUrl + shortCode;
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl( String shortCode ) {
        return urlMappingRepository.findByShortCode(shortCode).map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new ResourceNotFoundException("Short code not found: " + shortCode));
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        String ALP_NUM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for ( int i = 0; i < SHORT_CODE_LENGTH; i++ ) {
            int rndIndex = random.nextInt(ALP_NUM_CHARS.length());
            sb.append(ALP_NUM_CHARS.charAt(rndIndex));
        }

        return sb.toString();
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch ( MalformedURLException e) {
            return false;
        }
    }
}

package com.demo.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name="url_mapping",
    indexes = {
        @Index(name = "idx_original_url", columnList = "originalUrl", unique = true)
    })
public class UrlMapping {
    @Id
    @Column(length=6)
    private String shortCode;

    @Column(nullable = false, length = 500)
    private String originalUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UrlMapping(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }

    @PrePersist
    protected void onCreate() {
        if ( this.createdAt == null ) {
            this.createdAt = LocalDateTime.now();
        }
    }
}

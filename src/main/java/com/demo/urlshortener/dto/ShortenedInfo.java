package com.demo.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenedInfo {
    private String shortcode;
    private String url;
}

package com.example.nesta.dto;

public record ApartmentImageDto(
        Long id,
        String url,
        Integer width,
        Integer height,
        String contentType,
        long sizeBytes
) {}

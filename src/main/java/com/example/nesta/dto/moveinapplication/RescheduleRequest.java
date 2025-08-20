package com.example.nesta.dto.moveinapplication;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleRequest (
        @NotNull LocalDateTime updatedViewingDateTime
) {}

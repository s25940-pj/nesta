package com.example.nesta.dto.moveinapplication;

import com.example.nesta.model.enums.MoveInApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DecisionRequest(
        @NotNull MoveInApplicationStatus status,
        @Size(max = 300) String reason
) {}


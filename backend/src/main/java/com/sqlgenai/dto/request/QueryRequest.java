package com.sqlgenai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QueryRequest(
    @NotBlank(message = "Question cannot be blank")
    @Size(max = 1000, message = "Question is too long")
    String question,
    
    String schemaName
) {}

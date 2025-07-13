package com.mcpdemo.movieAgent.mcp.protocol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record McpRequestContext(
        @NotBlank
        String provider,
        @NotBlank(message = "User prompt cannot be blank.")
        @Size(min = 20, max = 1000, message = "Prompt must be between 20 and 1000 characters.")
        String userPrompt
) {
}

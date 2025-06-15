package com.mcpdemo.movieAgent.mcp.protocol.dto.common;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        int statusCode,
        String message,
        String path,
        LocalDateTime timestamp,
        List<String> validationErrors
) {
}

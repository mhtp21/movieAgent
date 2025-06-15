package com.mcpdemo.movieAgent.mcp.protocol.dto.common;

import java.util.List;

public record MovieSuggestion(
        String title,
        String plotSummary,
        List<String> genres
) {
}

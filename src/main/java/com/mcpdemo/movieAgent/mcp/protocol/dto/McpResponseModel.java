package com.mcpdemo.movieAgent.mcp.protocol.dto;

import com.mcpdemo.movieAgent.mcp.protocol.dto.common.MovieSuggestion;

import java.util.List;

public record McpResponseModel(
        List<MovieSuggestion> suggestions
) {
}

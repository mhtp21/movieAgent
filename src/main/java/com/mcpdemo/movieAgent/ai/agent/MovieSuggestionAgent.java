package com.mcpdemo.movieAgent.ai.agent;

import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import reactor.core.publisher.Mono;

public interface MovieSuggestionAgent {
    Mono<McpResponseModel> getSuggestions(McpRequestContext context);
}

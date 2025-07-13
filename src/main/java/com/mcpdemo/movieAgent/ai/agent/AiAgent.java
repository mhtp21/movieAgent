
package com.mcpdemo.movieAgent.ai.agent;

import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import reactor.core.publisher.Mono;

public interface AiAgent {
    String getName();
    boolean supports(McpRequestContext context);
    Mono<McpResponseModel> getSuggestions(McpRequestContext context);
}

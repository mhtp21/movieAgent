
package com.mcpdemo.movieAgent.ai.orchestration;

import com.mcpdemo.movieAgent.ai.registry.AgentRegistry;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final AgentRegistry agentRegistry;

    public Mono<McpResponseModel> route(McpRequestContext context) {
        return agentRegistry.resolve(context).getSuggestions(context);
    }
}

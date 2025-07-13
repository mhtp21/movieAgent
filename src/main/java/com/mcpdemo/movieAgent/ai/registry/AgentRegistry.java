
package com.mcpdemo.movieAgent.ai.registry;

import com.mcpdemo.movieAgent.ai.agent.AiAgent;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AgentRegistry {

    private final List<AiAgent> agents;

    public AiAgent resolve(McpRequestContext context) {
        return agents.stream()
                .filter(agent -> agent.supports(context))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No suitable agent found for context: " + context));
    }
}

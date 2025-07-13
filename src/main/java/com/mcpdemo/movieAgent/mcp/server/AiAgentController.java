
package com.mcpdemo.movieAgent.mcp.server;

import com.mcpdemo.movieAgent.ai.orchestration.AgentOrchestrator;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import com.mcpdemo.movieAgent.mcp.protocol.dto.common.ApiError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AI Movie MCP Server (Dynamic Agent)", description = "Routes requests to dynamically selected AI agent based on the context")
@RestController
@RequestMapping("/api/v1/mcp")
@RequiredArgsConstructor
public class AiAgentController {

    private final AgentOrchestrator orchestrator;

    @Operation(
            summary = "Generate Movie Suggestions using dynamic AI agent",
            description = "This endpoint dynamically routes the prompt to the appropriate AI agent based on the 'provider' field in the request body. "
                    + "Supported values include 'openai', 'gemini', etc. The agent generates a list of relevant movie suggestions in JSON format.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = McpRequestContext.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "OpenAI Example",
                                            summary = "OpenAI-based Movie Prompt",
                                            value = "{\"provider\": \"openai\", \"userPrompt\": \"Suggest time-travel science fiction movies with strong female leads\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful response with movie suggestions",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = McpResponseModel.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Validation failed",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PostMapping("/suggest")
    public Mono<ResponseEntity<McpResponseModel>> suggest(@Valid @RequestBody McpRequestContext context) {
        return orchestrator.route(context).map(ResponseEntity::ok);
    }
}

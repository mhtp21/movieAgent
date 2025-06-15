package com.mcpdemo.movieAgent.mcp.server;

import com.mcpdemo.movieAgent.ai.agent.MovieSuggestionAgent;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import com.mcpdemo.movieAgent.mcp.protocol.dto.common.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "AI Movie Agent MCP Server (OpenAI)", description = "Provides movie suggestions based on user prompts using the Model-Context-Protocol.")
@RestController
@RequestMapping("/api/v1/mcp/agent")
@RequiredArgsConstructor
public class MovieAgentMcpServer {

    private final MovieSuggestionAgent  movieSuggestionAgent;

    @Operation(
            summary = "Generate Movie Suggestions (Reactive)",
            description = "Accepts a user-provided context (a movie theme or plot) and returns a list of AI-generated movie suggestions in a non-blocking way.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved movie suggestions.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = McpResponseModel.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request: The provided context is invalid (e.g., too short, or blank).",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred on the server.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PostMapping("/suggest-movies")
    public Mono<ResponseEntity<McpResponseModel>> getMovieSuggestions(@Valid @RequestBody Mono<McpRequestContext> contextMono){

        return contextMono.flatMap(movieSuggestionAgent::getSuggestions)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
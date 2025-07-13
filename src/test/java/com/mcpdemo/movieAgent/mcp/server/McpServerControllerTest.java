
package com.mcpdemo.movieAgent.mcp.server;

import com.mcpdemo.movieAgent.ai.orchestration.AgentOrchestrator;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import com.mcpdemo.movieAgent.mcp.protocol.dto.common.MovieSuggestion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(AiAgentController.class)
public class McpServerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AgentOrchestrator orchestrator;

    @Test
    void shouldReturnMovieSuggestionsFromOpenAi() {
        McpRequestContext request = new McpRequestContext("openai", "Suggest movies with robots and AI");
        McpResponseModel mockResponse = new McpResponseModel(List.of(
            new MovieSuggestion("Ex Machina", "A programmer is invited to administer a Turing test...", List.of("Sci-Fi", "Thriller"))
        ));

        Mockito.when(orchestrator.route(Mockito.any())).thenReturn(Mono.just(mockResponse));

        webTestClient.post()
            .uri("/api/v1/mcp/suggest")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.suggestions[0].title").isEqualTo("Ex Machina");
    }
}

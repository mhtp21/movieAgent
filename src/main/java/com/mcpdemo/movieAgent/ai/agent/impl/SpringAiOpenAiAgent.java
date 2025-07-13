package com.mcpdemo.movieAgent.ai.agent.impl;

import com.mcpdemo.movieAgent.ai.agent.AiAgent;
import com.mcpdemo.movieAgent.ai.prompt.PromptEngine;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiOpenAiAgent implements AiAgent {
    @Override
    public String getName() {
        return "openai";
    }

    @Override
    public boolean supports(McpRequestContext context) {
        return context != null && "openai".equalsIgnoreCase(context.provider());
    }


    private final ChatClient chatClient;

    private final PromptEngine promptEngine;


    @Override
    public Mono<McpResponseModel> getSuggestions(McpRequestContext context) {
        log.info("Reactive AI Agent (OpenAI) processing request for prompt: '{}'", context.userPrompt());

        var outputParser = new BeanOutputParser<>(McpResponseModel.class);
        Map<String, Object> promptParameters = Map.of(
                "prompt", context.userPrompt(),
                "format", outputParser.getFormat()
        );

        Prompt prompt = promptEngine.loadPrompt("movie-suggestion", promptParameters);

        return Mono.fromCallable(() -> {
                    log.info("Executing blocking AI call on a separate thread...");
                    return chatClient.call(prompt);
                })
                .subscribeOn(Schedulers.boundedElastic()).doOnNext(response -> log.info("Received response from AI, starting to parse."))
                .map(chatResponse -> outputParser.parse(chatResponse.getResult().getOutput().getContent()))
                .doOnSuccess(model -> log.info("Successfully parsed AI response into McpResponseModel with {} suggestions.", model.suggestions().size()))
                .doOnError(e -> log.error("Error during reactive AI call or parsing", e));
    }
}

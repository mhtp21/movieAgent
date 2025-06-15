package com.mcpdemo.movieAgent.ai.agent.impl;

import com.mcpdemo.movieAgent.ai.agent.MovieSuggestionAgent;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpRequestContext;
import com.mcpdemo.movieAgent.mcp.protocol.dto.McpResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiOpenAiAgent implements MovieSuggestionAgent {

    private final ChatClient chatClient;

    private final PromptTemplate systemPromptTemplate = new PromptTemplate("""
            You are a world-class movie expert. Based on the user's theme provided in the "{prompt}" variable,
            generate a list of all relevant movie suggestions.
    
            For each movie, provide:
            - the title
            - a 2-3 sentence plot summary
            - a list of its main genres
    
            Your response MUST strictly follow the JSON format instructions provided by the system.
            The JSON format instructions are:
            
            {format}
            """);


    @Override
    public Mono<McpResponseModel> getSuggestions(McpRequestContext context) {
        log.info("Reactive AI Agent (OpenAI) processing request for prompt: '{}'", context.userPrompt());

        var outputParser = new BeanOutputParser<>(McpResponseModel.class);
        Map<String, Object> promptParameters = Map.of(
                "prompt", context.userPrompt(),
                "format", outputParser.getFormat()
        );

        Prompt prompt = systemPromptTemplate.create(promptParameters);

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

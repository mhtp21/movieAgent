package com.mcpdemo.movieAgent.ai.prompt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromptEngine {

    private final ResourceLoader resourceLoader;

    public Prompt loadPrompt(String templateName, Map<String, Object> variables) {
        String resourcePath = "classpath:prompts/" + templateName + ".yaml";
        try (InputStream is = resourceLoader.getResource(resourcePath).getInputStream()) {
            Yaml yaml = new Yaml();
            // Tüm belgeyi önce Map olarak yükle
            @SuppressWarnings("unchecked")
            Map<String, Object> document = yaml.load(is);

            // "template" alanını alalım
            Object rawTemplate = document.get("template");
            final String templateContent;
            if (rawTemplate instanceof String) {
                templateContent = (String) rawTemplate;
            }
            else if (rawTemplate instanceof List || rawTemplate instanceof Map) {
                // Yanlış parse edilmişse, YAML dump ile String’e çevir
                templateContent = yaml.dump(rawTemplate);
            }
            else {
                throw new IllegalArgumentException(
                        "Beklenmeyen tipte ‘template’: " +
                                (rawTemplate == null ? "null" : rawTemplate.getClass().getName())
                );
            }

            // PromptTemplate ile prompt’u oluştur
            PromptTemplate template = new PromptTemplate(templateContent);
            return template.create(variables);

        } catch (Exception e) {
            log.error("Failed to load prompt template '{}': {}", templateName, e.getMessage(), e);
            throw new RuntimeException("Prompt template loading failed", e);
        }
    }
}

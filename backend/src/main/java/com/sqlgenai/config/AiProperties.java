package com.sqlgenai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String provider = "gemini";
    private String geminiApiKey = "";
    private String openaiApiKey = "";

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }

    public void setOpenaiApiKey(String openaiApiKey) {
        this.openaiApiKey = openaiApiKey;
    }

    public boolean isGeminiConfigured() {
        return geminiApiKey != null && !geminiApiKey.isBlank();
    }

    public boolean isOpenAiConfigured() {
        return openaiApiKey != null && !openaiApiKey.isBlank();
    }
}

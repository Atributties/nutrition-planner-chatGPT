package com.example.nutritionplanner.services;

import com.example.nutritionplanner.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.ArrayList;
import java.util.List;

@Service
public class ChatGPTService {

    // Brug konstanter for beskedroller
    private static final String USER_ROLE = "user";
    private static final String SYSTEM_ROLE = "system";

    @Value("${gpt.model}")
    private String gptModel;

    @Value("${gpt.api.key}")
    private String gptApiKey;

    @Value("${gpt.api.url}")
    private String gptApiUrl;

    private final WebClient.Builder webClientBuilder;

    public ChatGPTService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public List<Choice> fetchChatGPT(ChatRequestFromUser chatRequestFromUser) {
        ChatRequest chatRequest = createChatRequest(chatRequestFromUser);
        ChatResponse response = sendChatRequest(chatRequest);
        return response.getChoices();
    }

    // Opret beskeder baseret på brugerinput
    private ChatRequest createChatRequest(ChatRequestFromUser chatRequestFromUser) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel(gptModel);
        List<Message> lstMessages = new ArrayList<>();

        String userMessage = String.format("I am a %s and my goal is to %s. My weight is %.2f kg and my height is %.2f cm.",
                chatRequestFromUser.getUserInformation().getGender(),
                chatRequestFromUser.getNutritionType(),
                chatRequestFromUser.getUserInformation().getWeight(),
                chatRequestFromUser.getUserInformation().getHeight());

        lstMessages.add(new Message(SYSTEM_ROLE, "You are a helpful assistant."));
        lstMessages.add(new Message(USER_ROLE, userMessage));
        lstMessages.add(new Message(USER_ROLE, "Can you show me my daily nutritional needs?"));
        lstMessages.add(new Message(USER_ROLE, "Can you help me create a meal plan for the day?"));

        chatRequest.setMessages(lstMessages);
        chatRequest.setN(chatRequestFromUser.getNumberOfDays());
        chatRequest.setTemperature(1);
        chatRequest.setMaxTokens(250);
        chatRequest.setStream(false);
        chatRequest.setPresencePenalty(1);

        return chatRequest;
    }

    // Send chatanmodningen ved hjælp af WebClient
    private ChatResponse sendChatRequest(ChatRequest chatRequest) {
        return webClientBuilder.baseUrl(gptApiUrl)
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(gptApiKey))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();
    }

}

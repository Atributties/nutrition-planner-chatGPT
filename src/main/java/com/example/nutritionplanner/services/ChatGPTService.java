package com.example.nutritionplanner.services;


import com.example.nutritionplanner.dto.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatGPTService {

    private final WebClient webClient;

    public ChatGPTService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    public List<Choice> fetchChatGPT(ChatRequestFromUser chatRequestFromUser) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo"); //vælg rigtig model. se powerpoint
        List<Message> lstMessages = new ArrayList<>(); //en liste af messages med roller

        lstMessages.add(new Message("user", "I am a " + chatRequestFromUser.getUserInformation().getGender() +
                " and my goal is to " + chatRequestFromUser.getNutritionType() +
                ". My weight is " + chatRequestFromUser.getUserInformation().getWeight() +
                " and my height is " + chatRequestFromUser.getUserInformation().getHeight() +
                "."));
        lstMessages.add(new Message("user", "Can you show me my daily nutritional needs?"));


        lstMessages.add(new Message("user", "Can you help me create a meal plan for the day?"));

        chatRequest.setMessages(lstMessages);
        chatRequest.setN(chatRequestFromUser.getNumberOfDays()); //n er antal svar fra chatgpt
        chatRequest.setTemperature(1); //jo højere jo mere fantasifuldt svar (se powerpoint)
        chatRequest.setMaxTokens(250); //længde af svar
        chatRequest.setStream(false); //stream = true, er for viderekomne, der kommer flere svar asynkront
        chatRequest.setPresencePenalty(1); //noget med ikke at gentage sig. se powerpoint

        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth("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"))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        List<Choice> lst = response.getChoices();

        return lst;


    }

}

package com.example.nutritionplanner.services;

import com.example.nutritionplanner.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
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


    public String createUserMessage(ChatRequestFromUser chatRequestFromUser) {
        return String.format("I wont a meal-plan for the day! I am a %s and my goal is to %s. My weight is %.2f kg and my height is %.2f cm. I am %d years old. My activity level is %s.",
                chatRequestFromUser.getUserInformation().getGender(),
                chatRequestFromUser.getNutritionType(),
                chatRequestFromUser.getUserInformation().getWeight(),
                chatRequestFromUser.getUserInformation().getHeight(),
                chatRequestFromUser.getUserInformation().getAge(),
                chatRequestFromUser.getUserInformation().getActivityLevel());
    }

    public String mealPlanExample() {
        return """
                        Breakfast:
                        - Oatmeal: 40 grams (1 portion)
                        - Skimmed milk: 200 ml (1 glass)
                        - Banana: 1 piece

                        First Snack:
                        - Apple: 1 piece

                        Lunch:
                        - Chicken breast: 150 grams (1 portion)
                        - Brown rice: 75 grams (1/2 cup)
                        - Broccoli: 100 grams (1 portion)
                        - Carrots: 75 grams (1/2 cup)

                        Second Snack:
                        - Carrot sticks with hummus: 1 portion
                        
                        Afternoon Snack:
                        - Greek yogurt: 150 grams (1 portion)
                        - Blueberries: 75 grams (1/2 cup)
                        - Almonds: 20 grams (1 handful)

                        Dinner:
                        - Salmon: 150 grams (1 portion)
                        - Quinoa: 75 grams (1/2 cup)
                        - Asparagus: 100 grams (1 portion)
                        - Tomatoes: 75 grams (1/2 cup)
                        
                        After Dinner Snack:
                        - Apple: 1 piece
                        """

                ;
    }


    // Opret beskeder baseret på brugerinput
    private ChatRequest createChatRequest(ChatRequestFromUser chatRequestFromUser) {
        ChatRequest chatRequest = new ChatRequest();

        chatRequest.setModel(gptModel);

        List<Message> lstMessages = new ArrayList<>();
        lstMessages.add(new Message(SYSTEM_ROLE, "U be helpful diet assistant wey go to create meal plan based on the input information u get using the following recipe format but with different ingredients every time" + mealPlanExample() + "You should always follow the recip structure but make some other meals and ingredients every time."));
        lstMessages.add(new Message(USER_ROLE, createUserMessage(chatRequestFromUser)));

        chatRequest.setMessages(lstMessages);
        chatRequest.setN(1);
        chatRequest.setTemperature(1.0);
        chatRequest.setMaxTokens(400);
        chatRequest.setStream(false);
        chatRequest.setPresencePenalty(1);

        return chatRequest;
    }
    // Send chatanmodningen ved hjælp af WebClient
    private ChatResponse sendChatRequest(ChatRequest chatRequest) {
        Instant startTime = Instant.now(); // Capture the start time

        ChatResponse response = webClientBuilder.baseUrl(gptApiUrl)
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(gptApiKey))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        Instant endTime = Instant.now(); // Capture the end time
        Duration duration = Duration.between(startTime, endTime);

        System.out.println("Time taken to receive data: " + duration.toMillis() + " milliseconds");

        return response;
    }

}

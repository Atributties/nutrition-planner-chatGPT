package com.example.nutritionplanner.controllers;


import com.example.nutritionplanner.dto.ChatRequest;
import com.example.nutritionplanner.dto.ChatResponse;
import com.example.nutritionplanner.dto.Choice;
import com.example.nutritionplanner.dto.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatGPTRestController {


    private final WebClient webClient;

    public ChatGPTRestController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    @GetMapping("/chat")
    public List<Choice> chatWithGPT() {
        ChatRequest chatRequest = new ChatRequest(); //ChatRequest objekt har jeg dannet med https://www.jsonschema2pojo.org/ værktøj
        chatRequest.setModel("gpt-3.5-turbo"); //vælg rigtig model. se powerpoint
        List<Message> lstMessages = new ArrayList<>(); //en liste af messages med roller
        lstMessages.add(new Message("system", "You are a helpful assistant."));
        lstMessages.add(new Message("user", "Can you make a one day meal plan?"));
        chatRequest.setMessages(lstMessages);
        chatRequest.setN(5); //n er antal svar fra chatgpt
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

        /*ChatResponse response = webClient.post()  , man skal kalde med post
                .contentType(MediaType.APPLICATION_JSON)  , vigtigt at huske
                .headers(h -> h.setBearerAuth("sk-kB0mg16RNk3IZlaVdjQ6Sv8Jb9KP4b0")) , sæt openapi key ind. brug evt env variable i application.properties og @Value
                .bodyValue(chatRequest) her indsættes spørgsmål som er pakket ind i et ChatRequest objekt
                .retrieve() går igang med at hente
                .bodyToMono(ChatResponse.class) der kommer json tilbage, og dette json skal bygges om til Java, angiv Java klasse, her vores ChatResponse
                .block(); her venter vi på at webClient læser restApi og bygger Java
       */


    }



}

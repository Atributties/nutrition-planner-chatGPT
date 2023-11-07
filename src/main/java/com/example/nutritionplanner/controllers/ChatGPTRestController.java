package com.example.nutritionplanner.controllers;


import com.example.nutritionplanner.dto.*;
import com.example.nutritionplanner.services.ChatGPTService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ChatGPTRestController {


    final ChatGPTService chatGPTService;

    public ChatGPTRestController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }


    @PostMapping("/chat")
    public List<Choice> chatWithGPT(@RequestBody ChatRequestFromUser chatRequestFromUser) {
        System.out.println(chatRequestFromUser.toString());
        return chatGPTService.fetchChatGPT(chatRequestFromUser);

    }



}

package com.example.nutritionplanner.dto;


import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ChatRequestFromUser {
    private int id;
    private String nutritionType;
    private int numberOfDays;
    @OneToOne
    private UserInformation userInformation;
}


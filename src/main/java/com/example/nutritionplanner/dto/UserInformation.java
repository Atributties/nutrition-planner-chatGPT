package com.example.nutritionplanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInformation {
    private int id;
    private String gender;
    private int age;
    private double weight;
    private double height;
    private String activityLevel;
}


package com.example.nutritionplanner.dto;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInformation {
    private int id;
    private String gender;
    private String name;
    private int age;
    private double weight;
    private double height;
}


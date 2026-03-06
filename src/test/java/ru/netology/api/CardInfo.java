package ru.netology.api;

import lombok.Data;

@Data
public class CardInfo {
    private String id;
    private String number;
    private int balance_in_kopecks;
}
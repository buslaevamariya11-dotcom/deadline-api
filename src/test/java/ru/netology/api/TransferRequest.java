package ru.netology.api;

import lombok.Value;

@Value
public class TransferRequest {
    String from;
    String to;
    int amount;
}

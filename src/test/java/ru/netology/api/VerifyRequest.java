package ru.netology.api;

import lombok.Value;

@Value
public class VerifyRequest {
    String login;
    String code;
}
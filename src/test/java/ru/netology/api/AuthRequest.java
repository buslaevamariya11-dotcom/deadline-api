package ru.netology.api;

import lombok.Value;

@Value
public class AuthRequest {
    String login;
    String password;
}

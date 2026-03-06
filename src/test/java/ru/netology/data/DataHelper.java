package ru.netology.data;

import lombok.Value;

public class DataHelper {

    private DataHelper() {
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static CardInfo getFirstCard() {
        return new CardInfo("5559 0000 0000 0002");
    }

    public static CardInfo getSecondCard() {
        return new CardInfo("5559 0000 0000 0008");
    }

    public static int getTransferAmount(int balance) {
        return balance / 2;
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class CardInfo {
        String number;
    }
}

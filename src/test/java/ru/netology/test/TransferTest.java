package ru.netology.test;

import org.junit.jupiter.api.Test;
import ru.netology.api.ApiHelper;
import ru.netology.data.DataHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    @Test
    void shouldTransferMoneyBetweenCards() {

        var auth = DataHelper.getAuthInfo();
        var card1 = DataHelper.getFirstCard();
        var card2 = DataHelper.getSecondCard();

        String token = ApiHelper.loginAndGetToken(auth);

        int balance1 = ApiHelper.getCardBalance(token, card1.getNumber());
        int balance2 = ApiHelper.getCardBalance(token, card2.getNumber());

        int amount = 5000;

        ApiHelper.transfer(token, card1.getNumber(), card2.getNumber(), amount);

        int balance1after = ApiHelper.getCardBalance(token, card1.getNumber());
        int balance2after = ApiHelper.getCardBalance(token, card2.getNumber());

        assertEquals(balance1 - amount, balance1after);
        assertEquals(balance2 + amount, balance2after);
    }
}
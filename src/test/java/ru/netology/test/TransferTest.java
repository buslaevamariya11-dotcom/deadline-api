package ru.netology.test;

import org.junit.jupiter.api.Test;
import ru.netology.api.ApiHelper;
import ru.netology.data.DataHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    @Test
    void shouldTransferMoneyBetweenCards() {

        var authInfo = DataHelper.getAuthInfo();
        var firstCard = DataHelper.getFirstCard();
        var secondCard = DataHelper.getSecondCard();

        String token = ApiHelper.loginAndGetToken(authInfo);

        int balanceFirstBefore =
                ApiHelper.getCardBalance(token, firstCard.getNumber());

        int balanceSecondBefore =
                ApiHelper.getCardBalance(token, secondCard.getNumber());

        int amount = DataHelper.getTransferAmount(balanceFirstBefore);

        ApiHelper.transfer(
                token,
                firstCard.getNumber(),
                secondCard.getNumber(),
                amount
        );

        int balanceFirstAfter =
                ApiHelper.getCardBalance(token, firstCard.getNumber());

        int balanceSecondAfter =
                ApiHelper.getCardBalance(token, secondCard.getNumber());

        assertEquals(balanceFirstBefore - amount, balanceFirstAfter);
        assertEquals(balanceSecondBefore + amount, balanceSecondAfter);
    }
}
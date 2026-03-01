package ru.netology.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 9999;
    }

    @BeforeEach
    void prepareData() {
        DbUtils.cleanAuthCodes();
        DbUtils.resetBalances();
    }

    private String loginAndGetToken(String login, String password) {

        given()
                .header("Content-Type", "application/json")
                .body("{\"login\":\"" + login + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);

        String code = DbUtils.getVerificationCode(login);
        assertThat(code, notNullValue());

        Response verificationResponse =
                given()
                        .header("Content-Type", "application/json")
                        .body("{\"login\":\"" + login + "\",\"code\":\"" + code + "\"}")
                        .when()
                        .post("/api/auth/verification")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        String token = verificationResponse.path("token");
        assertThat(token, notNullValue());

        return token;
    }

    @Test
    void shouldTransferMoneyBetweenCards() {

        String token = loginAndGetToken("vasya", "qwerty123");

        Response before =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/cards")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        int balanceFrom = before.jsonPath().getInt("[0].balance");
        int balanceTo = before.jsonPath().getInt("[1].balance");

        String requestBody = "{"
                + "\"from\":\"5559 0000 0000 0002\","
                + "\"to\":\"5559 0000 0000 0001\","
                + "\"amount\":5000"
                + "}";

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);

        Response after =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/cards")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        int newBalanceFrom = after.jsonPath().getInt("[0].balance");
        int newBalanceTo = after.jsonPath().getInt("[1].balance");

        assertThat(newBalanceFrom, equalTo(balanceFrom - 5000));
        assertThat(newBalanceTo, equalTo(balanceTo + 5000));
    }

    @Test
    void shouldNotTransferMoreThanBalance() {

        String token = loginAndGetToken("vasya", "qwerty123");

        Response before =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/cards")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        int balanceFrom = before.jsonPath().getInt("[0].balance");
        int balanceTo = before.jsonPath().getInt("[1].balance");

        String requestBody = "{"
                + "\"from\":\"5559 0000 0000 0002\","
                + "\"to\":\"5559 0000 0000 0001\","
                + "\"amount\":20000"
                + "}";

        Response transferResponse =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(requestBody)
                        .when()
                        .post("/api/transfer")
                        .then()
                        .extract()
                        .response();

        Response after =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/cards")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        int newBalanceFrom = after.jsonPath().getInt("[0].balance");
        int newBalanceTo = after.jsonPath().getInt("[1].balance");

        // Баланс не должен изменяться
        assertThat(newBalanceFrom, equalTo(balanceFrom));
        assertThat(newBalanceTo, equalTo(balanceTo));
    }
}
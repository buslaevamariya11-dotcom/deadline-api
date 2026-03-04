package ru.netology.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.db.DbUtils;

import static io.restassured.RestAssured.given;

public class TransferApiTest {

    private String getToken() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 9999;

        String loginBody =
                "{ \"login\": \"vasya\", \"password\": \"qwerty123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .post("/api/auth")
                .then()
                .statusCode(200);

        String code = DbUtils.getAuthCode("vasya");

        String verifyBody =
                "{ \"login\": \"vasya\", \"code\": \"" + code + "\" }";

        return given()
                .contentType(ContentType.JSON)
                .body(verifyBody)
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @BeforeEach
    void clearAuth() {
        DbUtils.clearAuthCodes();
    }

    @Test
    void shouldTransferMoneyBetweenCards() {

        String token = getToken();

        String transferBody =
                "{ \"from\": \"5559 0000 0000 0002\", " +
                        "\"to\": \"5559 0000 0000 0008\", " +
                        "\"amount\": 5000 }";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(transferBody)
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldNotTransferMoreThanBalance() {

        String token = getToken();

        String transferBody =
                "{ \"from\": \"5559 0000 0000 0002\", " +
                        "\"to\": \"5559 0000 0000 0008\", " +
                        "\"amount\": 999999999 }";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(transferBody)
                .post("/api/transfer")
                .then()
                .statusCode(400); // ожидаем ошибку
    }
}
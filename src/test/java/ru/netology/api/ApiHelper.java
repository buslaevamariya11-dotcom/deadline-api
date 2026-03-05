package ru.netology.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import ru.netology.data.DataHelper;
import ru.netology.db.DbUtils;

import static io.restassured.RestAssured.given;

public class ApiHelper {

    static {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 9999;
    }

    private ApiHelper() {
    }

    public static String loginAndGetToken(DataHelper.AuthInfo authInfo) {

        given()
                .contentType(ContentType.JSON)
                .body(new AuthRequest(authInfo.getLogin(), authInfo.getPassword()))
                .post("/api/auth")
                .then()
                .statusCode(200);

        String code = DbUtils.getAuthCode(authInfo.getLogin());

        return given()
                .contentType(ContentType.JSON)
                .body(new VerifyRequest(authInfo.getLogin(), code))
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static int getCardBalance(String token, String cardNumber) {

        return given()
                .header("Authorization", "Bearer " + token)
                .get("/api/cards")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getInt("find { it.number == '" + cardNumber + "' }.balance");
    }

    public static void transfer(String token, String from, String to, int amount) {

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new TransferRequest(from, to, amount))
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }
}

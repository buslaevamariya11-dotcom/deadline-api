package ru.netology.api;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {

    private static final String URL = "jdbc:mysql://localhost:3306/app";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";

    private static final QueryRunner runner = new QueryRunner();

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String getVerificationCode(String login) {
        String sql = "SELECT code FROM auth_codes " +
                "WHERE user_id = (SELECT id FROM users WHERE login = ?) " +
                "ORDER BY created DESC LIMIT 1;";
        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>(), login);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanAuthCodes() {
        String sql = "DELETE FROM auth_codes;";
        try (Connection conn = getConnection()) {
            runner.update(conn, sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetBalances() {
        String sql = "UPDATE cards SET balance_in_kopecks = 1000000;";
        try (Connection conn = getConnection()) {
            runner.update(conn, sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
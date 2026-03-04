package ru.netology.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {

    private static final QueryRunner runner = new QueryRunner();

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app",
                "app",
                "pass"
        );
    }

    public static String getAuthCode(String login) {

        String sql =
                "SELECT ac.code " +
                        "FROM auth_codes ac " +
                        "JOIN users u ON ac.user_id = u.id " +
                        "WHERE u.login = ? " +
                        "ORDER BY ac.created DESC " +
                        "LIMIT 1;";

        try (Connection conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>(), login);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearAuthCodes() {

        try (Connection conn = getConnection()) {
            runner.update(conn, "DELETE FROM auth_codes;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

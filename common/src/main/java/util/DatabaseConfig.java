package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/chat_db");
        String user = System.getenv().getOrDefault("DB_USER", "user");
        String password = System.getenv().getOrDefault("DB_PASS", "password");

        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);

        config.setInitializationFailTimeout(30000);

        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

package repository;

import exception.HashPasswordException;
import model.CreateUserDto;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import util.DatabaseConfig;

import javax.xml.crypto.Data;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class JdbcUserRepository implements UserRepository {

    @Override
    public User save(CreateUserDto createUserDto) {
        UUID uuid = UUID.randomUUID();

        String hashedRounded = BCrypt.hashpw(createUserDto.getPassword(), BCrypt.gensalt());

        String request = "INSERT INTO users (id, username, password_hash) VALUES (?, ?, ?)";
        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(request)
        ) {
            stmt.setObject(1, uuid);
            stmt.setObject(2, createUserDto.getUsername());
            stmt.setObject(3, hashedRounded);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving user", e);
        }

        User user = new User();
        user.setId(uuid);
        user.setUsername(createUserDto.getUsername());
        user.setPasswordHash(hashedRounded);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String request = "SELECT * FROM users WHERE username = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(request)
        ) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(UUID.fromString(rs.getString(1)));
                    user.setUsername(rs.getString(2));
                    user.setPasswordHash(rs.getString(3));

                    return Optional.of(user);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Not found user or some other exception", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findById(UUID id) {
        String request = "SELECT * FROM users WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(request)
        ) {
            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(UUID.fromString(rs.getString(1)));
                    user.setUsername(rs.getString(2));
                    user.setPasswordHash(rs.getString(3));

                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Not found user or some other exception", e);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String request = "SELECT * FROM users";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(request);
                ResultSet rs = stmt.executeQuery()
        ) {
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getObject("id", UUID.class));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));

                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Not found user or some other exception", e);
        }

        return users;
    }

    @Override
    public boolean isUserExists(String username) {
        String request = "SELECT * FROM users WHERE username = ?";

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement stmt = connection.prepareStatement(request)
        ) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Not found user or some other exception", e);
        }
    }

    @Override
    public boolean verifyPassword(String password, String passwordHash) {
        return BCrypt.checkpw(password, passwordHash);
    }
}

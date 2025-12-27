package repository;

import model.Message;
import util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcMessageRepository implements MessageRepository {

    @Override
    public Message save(Message message) {
        UUID uuid = UUID.randomUUID();
        String request = "INSERT INTO messages (id, sender_id, text) VALUES (?, ?, ?)";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(request)
        ) {
            stmt.setObject(1, uuid);
            stmt.setObject(2,  message.getSenderId());
            stmt.setString(3,  message.getText());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving message", e);
        }
        message.setId(uuid);
        message.setTimestamp(LocalDateTime.now());

        return message;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        String request = "SELECT * FROM messages";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(request);
                ResultSet rs = stmt.executeQuery()
                ) {
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getObject("id", UUID.class));
                message.setSenderId(rs.getObject("sender_id", UUID.class));
                message.setText(rs.getString("text"));
                message.setTimestamp(rs.getObject("created_at", LocalDateTime.class));
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all messages", e);
        }

        return messages;
    }
}

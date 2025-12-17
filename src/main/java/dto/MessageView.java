package dto;

import java.time.LocalDateTime;

public class MessageView {
    private final String username;
    private final String text;
    private final LocalDateTime timestamp;

    public MessageView(String username, String text, LocalDateTime timestamp) {
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return timestamp + " " + username + ": " + text;
    }
}

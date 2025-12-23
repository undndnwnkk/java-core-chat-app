package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    private UUID id;
    private UUID senderId;
    private String text;
    private LocalDateTime timestamp;

    public Message() {
    }

    public Message(UUID id, UUID senderId, String text, LocalDateTime timestamp) { // TODO Autogenerate Id
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return timestamp.toString() +
                " Sender ID: " + senderId.toString() +
                ": " + text;
    }
}

package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    private UUID id;
    private Long senderId;
    private String text;
    private LocalDateTime timestamp;

    public Message() {
    }

    public Message(UUID id, Long senderId, String text, LocalDateTime timestamp) { // TODO Autogenerate Id
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

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
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
}

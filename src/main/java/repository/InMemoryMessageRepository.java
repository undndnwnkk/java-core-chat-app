package repository;

import model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryMessageRepository implements MessageRepository{
    private Map<UUID, Message> messages;

    public InMemoryMessageRepository() {
        this.messages = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return messages.values().stream().toList();
    }
}

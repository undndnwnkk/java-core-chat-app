package service;

import model.Message;
import model.User;
import repository.InMemoryMessageRepository;

import java.time.LocalDateTime;

public class MessageService {
    private final InMemoryMessageRepository messageRepository;

    public MessageService(InMemoryMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void sendMessage(User user, String messageText) {
        Message message = new Message();
        message.setSenderId(user.getId());
        message.setText(messageText);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

    public String history() {
        StringBuilder sb = new StringBuilder();

        messageRepository.findAll().stream()
                .forEach(message -> sb.append(message.toString() + "\n"));

        return sb.toString();
    }
}

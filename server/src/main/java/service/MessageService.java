package service;

import model.MessageView;
import model.Message;
import model.User;
import repository.MessageRepository;

import java.time.LocalDateTime;

public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;

    public MessageService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
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

        messageRepository.findAll().forEach(m -> {
            String username = userService.findById(m.getSenderId()).getUsername();

            MessageView view = new MessageView(username, m.getText(), m.getTimestamp());
            sb.append(view).append("\n");
        });

        return sb.toString();
    }

}

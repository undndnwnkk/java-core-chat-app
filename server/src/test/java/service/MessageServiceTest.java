package service;

import model.Message;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @Test
    void sendMessage_ShouldSaveMessage_WhenAllCorrect() {
        User sender = new User();
        sender.setId(UUID.randomUUID());
        sender.setUsername("testUser");
        String text = "testMessage";

        messageService.sendMessage(sender, text);

        verify(messageRepository).save(messageCaptor.capture());

        Message caughtMessage = messageCaptor.getValue();

        assertEquals(text, caughtMessage.getText());
        assertEquals(sender.getId(), caughtMessage.getSenderId());
    }

    @Test
    void history_ShouldReturnMessages_WhenAllCorrect() {
        User sender = new User();
        sender.setId(UUID.randomUUID());
        sender.setUsername("testUser");

        List<Message> messages = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            Message message = new Message();
            message.setId(UUID.randomUUID());
            message.setSenderId(sender.getId());
            message.setText("testMessage" + counter);
            messages.add(message);
            counter++;
        }

        when(messageRepository.findAll()).thenReturn(messages);

        String history = messageService.history();

        assertTrue(history.contains("testMessage0"));
        assertTrue(history.contains("testMessage1"));
        assertTrue(history.contains("testMessage2"));
    }
}

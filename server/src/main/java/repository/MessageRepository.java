package repository;

import model.Message;

import java.util.List;

public interface MessageRepository {


    Message save(Message message);
    List<Message> findAll();
//    List<Message> findBySenderId(Long senderId);
}

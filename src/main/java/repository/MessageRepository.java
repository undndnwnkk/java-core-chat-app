package repository;

import model.Message;

import java.util.List;

public interface MessageRepository {


    void save(Message message);
    List<Message> findAll();
//    List<Message> findBySenderId(Long senderId);
}

package repository;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findAll();
    boolean isUserExists(String username);
}

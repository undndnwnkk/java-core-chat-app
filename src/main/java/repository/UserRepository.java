package repository;

import dto.CreateUserDto;
import model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(CreateUserDto createUserDto);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean isUserExists(String username);
}

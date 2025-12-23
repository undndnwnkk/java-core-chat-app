package repository;

import model.CreateUserDto;
import exception.HashPasswordException;
import model.User;
import java.security.MessageDigest;

import java.util.*;

public class InMemoryUserRepository implements UserRepository{
    private Map<UUID, User> users;

    public InMemoryUserRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public User save(CreateUserDto createUserDto) {
        User user = new User();
        user.setId(generateId());
        user.setUsername(createUserDto.getUsername());
        user.setPasswordHash(hashPassword(createUserDto.getPassword()));
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public boolean isUserExists(String username) {
        return users.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    private UUID generateId() {
        return UUID.randomUUID();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new HashPasswordException("Error hashing password", e);
        }
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}

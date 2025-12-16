package repository;

import model.User;

import java.util.*;

public class InMemoryUserRepository implements UserRepository{
    private Map<UUID, User> users;

    public InMemoryUserRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
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
}

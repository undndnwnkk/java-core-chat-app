package service;

import exception.IncorrectPasswordException;
import exception.UserAlreadyExistsException;
import model.User;
import repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(String username, String password) {
        if (userRepository.isUserExists(username)) {
            throw new UserAlreadyExistsException("User with this email already exists");
        } else {
            if (password.length() < 8) throw new IncorrectPasswordException("Password is too short");


        }
    }
}

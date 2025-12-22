package service;

import dto.CreateUserDto;
import exception.IncorrectPasswordException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.User;
import repository.UserRepository;

import java.util.UUID;


public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(CreateUserDto createUserDto) {
        if (userRepository.isUserExists(createUserDto.getUsername())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        } else {
            if (createUserDto.getPassword().length() < 8) throw new IncorrectPasswordException("Password is too short");
            return userRepository.save(createUserDto);
        }
    }

    public User login(CreateUserDto createUserDto) {
        User currentUser = userRepository.findByUsername(createUserDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userRepository.verifyPassword(createUserDto.getPassword(), currentUser.getPasswordHash())) {
            System.out.println("Login successful");
            return currentUser;
        } else {
            throw new IncorrectPasswordException("Incorrect password");
        }
    }
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}

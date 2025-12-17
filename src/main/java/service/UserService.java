package service;

import dto.CreateUserDto;
import exception.IncorrectPasswordException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.User;
import repository.InMemoryUserRepository;

public class UserService {
    private final InMemoryUserRepository userRepository;

    public UserService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(CreateUserDto createUserDto) {
        if (userRepository.isUserExists(createUserDto.getUsername())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        } else {
            if (createUserDto.getPassword().length() < 8) throw new IncorrectPasswordException("Password is too short");
            userRepository.save(createUserDto);
        }
    }

    public boolean login(CreateUserDto createUserDto) {
        User currentUser = userRepository.findByUsername(createUserDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userRepository.verifyPassword(createUserDto.getPassword(), currentUser.getPasswordHash())) {
            System.out.println("Login successful");
            return true;
        } else {
            throw new IncorrectPasswordException("Incorrect password");
        }
    }
}

package service;

import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.CreateUserDto;
import model.Message;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void login_ShouldReturnUser_WithCredentialsAreCorrect() {
        String username = "testUser";
        String password = "testPassword";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername(username);
        mockUser.setPasswordHash(hashedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(userRepository.verifyPassword(password, hashedPassword)).thenReturn(true);

        CreateUserDto loginDto = new CreateUserDto(username, password);
        User result = userService.login(loginDto);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        String username = "unknownUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        CreateUserDto loginDto = new CreateUserDto(username, "smth");
        assertThrows(UserNotFoundException.class, () -> userService.login(loginDto));

    }

    @Test
    void registerUser_ShouldReturnUser_WhenSuccess() {
        String username = "testUser";
        String password = "testPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(CreateUserDto.class))).thenAnswer(i -> {
            CreateUserDto dto = i.getArgument(0);
            return new User(dto.getUsername(), "any_hash");
        });

        CreateUserDto registerDto = new  CreateUserDto(username, password);
        User result = userService.registerUser(registerDto);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void registerUser_ShouldThrowException_WhenUserAlreadyExists() {
        String username = "testUser";
        String password = "testPassword";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername(username);
        mockUser.setPasswordHash(hashedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        CreateUserDto registerDto = new  CreateUserDto(username, hashedPassword);
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(registerDto));

    }
}

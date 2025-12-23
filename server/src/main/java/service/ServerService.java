package service;

import com.google.gson.Gson;
import model.CreateUserDto;
import exception.IncorrectPasswordException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.ClientCommandRequest;
import model.ServerResponse;
import model.User;
import repository.MessageRepository;
import repository.UserRepository;
import util.LoggerUtil;

import java.util.Map;
import java.util.UUID;

public class ServerService {
    private final UserService userService;
    private final MessageService messageService;
    private final Map<String, User> activeSessions; // token -> User

    private static final org.slf4j.Logger log = LoggerUtil.AUTH;

    public ServerService(UserRepository userRepo, MessageRepository msgRepo) {
        this.userService = new UserService(userRepo);
        this.messageService = new MessageService(msgRepo, userService);
        this.activeSessions = new java.util.HashMap<>();
    }

    public ServerResponse processRequest(Gson gson, String jsonRequest, String clientAddress) {
        log.debug("📨 {}: {}", clientAddress, jsonRequest);

        try {
            ClientCommandRequest request = gson.fromJson(jsonRequest, ClientCommandRequest.class);

            if (request == null || request.getType() == null) {
                log.warn("❌ {}: Invalid JSON or missing type", clientAddress);
                return new ServerResponse(false, null, "Invalid request format", null);
            }

            return switch (request.getType()) {
                case LOGIN -> handleLogin(gson, request, clientAddress);
                case REGISTER -> handleRegister(gson, request, clientAddress);
                case MSG -> handleMessage(request, clientAddress);
                case HISTORY -> handleHistory(request, clientAddress);
                case CHANGE_PROFILE -> handleChangeProfile(clientAddress);
                case EXIT -> handleExit(request, clientAddress);
                default -> new ServerResponse(false, null, "Unknown command", null);
            };

        } catch (Exception e) {
            log.error("💥 {}: Processing error", clientAddress, e);
            return new ServerResponse(false, null, "Server error", null);
        }
    }

    private ServerResponse handleLogin(Gson gson, ClientCommandRequest request, String clientAddress) {
        log.info("🔐 {}: LOGIN attempt", clientAddress);

        try {
            CreateUserDto dto = gson.fromJson(request.getArgs(), CreateUserDto.class);
            User user = userService.login(dto);
            String token = generateToken(user.getId());
            activeSessions.put(token, user);

            log.info("✅ {}: LOGIN success, user={}, token={}", clientAddress, user.getUsername(), token);
            return new ServerResponse(true, token, "Login successful", null);

        } catch (UserNotFoundException | IncorrectPasswordException e) {
            log.warn("❌ {}: LOGIN failed - {}", clientAddress, e.getMessage());
            return new ServerResponse(false, null, e.getMessage(), null);
        }
    }

    private ServerResponse handleRegister(Gson gson, ClientCommandRequest request, String clientAddress) {
        log.info("🔐 {}: REGISTER attempt", clientAddress);

        try {
            CreateUserDto dto = gson.fromJson(request.getArgs(), CreateUserDto.class);
            User user = userService.registerUser(dto);
            String token = generateToken(user.getId());
            activeSessions.put(token, user);

            log.info("✅ {}: REGISTER success, user={}, token={}", clientAddress, user.getUsername(), token);
            return new ServerResponse(true, token, "Account created", null);

        } catch (UserAlreadyExistsException | IncorrectPasswordException e) {
            log.warn("❌ {}: REGISTER failed - {}", clientAddress, e.getMessage());
            return new ServerResponse(false, null, e.getMessage(), null);
        }
    }

    private ServerResponse handleMessage(ClientCommandRequest request, String clientAddress) {
        User user = activeSessions.get(request.getToken());
        if (user == null) {
            log.warn("❌ {}: MSG rejected - invalid token", clientAddress);
            return new ServerResponse(false, null, "Unauthorized", null);
        }

        log.info("💬 {} ({}): '{}'", clientAddress, user.getUsername(), request.getArgs());
        messageService.sendMessage(user, request.getArgs());

        return new ServerResponse(true, null, "Message sent", null);
    }

    private ServerResponse handleHistory(ClientCommandRequest request, String clientAddress) {
        User user = activeSessions.get(request.getToken());
        if (user == null) {
            return new ServerResponse(false, null, "Unauthorized", null);
        }

        log.debug("📜 {} ({}): HISTORY request", clientAddress, user.getUsername());
        String history = messageService.history();
        return new ServerResponse(true, null, null, history);
    }

    private ServerResponse handleChangeProfile(String clientAddress) {
        log.info("🔄 {}: CHANGE_PROFILE - invalidate all sessions", clientAddress);
        activeSessions.clear();
        return new ServerResponse(true, null, "Enter new credentials", null);
    }

    private ServerResponse handleExit(ClientCommandRequest request, String clientAddress) {
        log.info("👋 {}: EXIT", clientAddress);
        activeSessions.remove(request.getToken());
        return new ServerResponse(true, null, "Goodbye!", null);
    }

    private String generateToken(UUID userId) {
        return userId.toString() + "_" + System.currentTimeMillis();
    }
}

import dto.CreateUserDto;
import exception.IncorrectPasswordException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.User;
import repository.InMemoryMessageRepository;
import repository.InMemoryUserRepository;
import service.MessageService;
import service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(new InMemoryUserRepository());
        MessageService messageService = new MessageService(new InMemoryMessageRepository(), userService);

        System.out.println("Welcome to this chat app!");
        System.out.println("Before get started you need to login/register");
        System.out.println("Choose an option: ");
        System.out.println("1. login");
        System.out.println("2. register");
        Scanner scanner = new Scanner(System.in);

        User currentUser = chooseEntryOption(scanner.nextLine(), userService);

        runChatLoop(scanner, currentUser, messageService);
    }

    private static User chooseEntryOption(String choice, UserService userService) {
        try {
            Scanner scanner = new Scanner(System.in);
            if (choice.equalsIgnoreCase("login") ||
                    choice.equals("1")) {
                while (true) {
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine();
                    System.out.print("\nEnter your password: ");
                    String password = scanner.nextLine();

                    CreateUserDto createUserDto = new CreateUserDto(username, password);
                    try {
                        return userService.login(createUserDto);
                    } catch (UserNotFoundException e) {
                        System.out.println("Not found user with this username, please try again\n");
                    }
                }
            } else if (choice.equalsIgnoreCase("register") ||
                    choice.equals("2")) {
                System.out.print("Enter your username: ");
                String username = scanner.nextLine();
                while (true) {
                    System.out.print("Enter your password: ");
                    String password = scanner.nextLine();
                    System.out.print("Confirm your password: ");
                    String confirmPassword = scanner.nextLine();

                    if (password.equals(confirmPassword)) {
                        try {
                            return userService.registerUser(new CreateUserDto(username, password));
                        } catch (IncorrectPasswordException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Passwords don't matches");
                        continue;
                    }

                    try {
                        userService.registerUser(new CreateUserDto(username, password));
                        System.out.println("Registration successful");
                        break;
                    } catch (UserAlreadyExistsException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                System.out.println("Unknown command, try again");
                chooseEntryOption(scanner.nextLine(), userService);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    private static void runChatLoop(Scanner scanner, User currentUser, MessageService messageService) {
        System.out.println("\nYou are logged in as " + currentUser.getUsername());
        System.out.println("Commands: /msg <text>, /history, /exit");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();

            if (line.equalsIgnoreCase("/exit")) {
                System.out.println("Goodbye!");
                break;
            } else if (line.equalsIgnoreCase("/history")) {
                System.out.println(messageService.history());
            } else if (line.startsWith("/msg ")) {
                String text = line.substring(5);
                messageService.sendMessage(currentUser, text);
            } else {
                System.out.println("Unknown command. Use /msg, /history, /exit");
            }
        }
    }
}

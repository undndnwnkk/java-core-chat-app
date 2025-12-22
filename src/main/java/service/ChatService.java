package service;

import dto.CreateUserDto;
import exception.IncorrectPasswordException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.User;

import java.util.Scanner;

public class ChatService {
    public ChatService(){}

    public User chooseEntryOption(Scanner scanner, String firstChoice, UserService userService) {
        try {
            String choice = firstChoice;
            while (true) {
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
                    String username = scanner.nextLine().trim();
                    while (true) {
                        System.out.print("Enter your password: ");
                        String password = scanner.nextLine();
                        System.out.print("Confirm your password: ");
                        String confirmPassword = scanner.nextLine();

                        if (password.equals(confirmPassword)) {
                            try {
                                return userService.registerUser(new CreateUserDto(username, password));
                            } catch (IncorrectPasswordException | UserAlreadyExistsException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Passwords don't matches");
                        }
                    }
                } else {
                    System.out.println("Unknown command, try again");
                    choice = scanner.nextLine();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    public void runChatLoop(Scanner scanner, User currentUser, UserService userService, MessageService messageService) {
        System.out.println("\nYou are logged in as " + currentUser.getUsername());
        System.out.println("Commands: /msg <text>, /history, /exit, /change_profile");

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
                text = text.trim();
                if (text.isEmpty()) {
                    System.out.println("Message can't be empty");
                    continue;
                }
                messageService.sendMessage(currentUser, text);
            } else if(line.equalsIgnoreCase("/change_profile")) {
                System.out.println("Hello again! Choose an option:");
                System.out.println("1. login");
                System.out.println("2. register");
                String choice = scanner.nextLine();
                currentUser = chooseEntryOption(scanner, choice, userService);
                System.out.println("You are now logged in as " + currentUser.getUsername());
            } else {
                System.out.println("Unknown command. Use /msg, /history, /exit");
            }
        }
    }
}

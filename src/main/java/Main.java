import dto.CreateUserDto;
import exception.IncorrectPasswordException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import repository.InMemoryUserRepository;
import service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(new InMemoryUserRepository());

        System.out.println("Welcome to this chat app!");
        System.out.println("Before get started you need to login/register");
        System.out.println("Choose an option: ");
        System.out.println("1. login");
        System.out.println("2. register");
        Scanner scanner = new Scanner(System.in);

        try {
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("login") ||
                    choice.equals("1")) {
                while (true) {
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine();
                    System.out.print("\nEnter your password: ");
                    String password = scanner.nextLine();

                    CreateUserDto createUserDto = new CreateUserDto(username, password);
                    try {
                        userService.login(createUserDto);
                        System.out.println("Login was successful");
                        break;
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
                            userService.registerUser(new CreateUserDto(username, password));
                            System.out.println("Registration successful");
                            break;
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
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

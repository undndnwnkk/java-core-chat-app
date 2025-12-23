package service;

import com.google.gson.Gson;
import model.ClientCommandRequest;
import model.CommandType;
import model.CreateUserDto;

import java.util.Scanner;

public class ClientService {
    private final Gson gson;
    private final Scanner scanner;

    public ClientService(Gson gson, Scanner scanner) {
        this.gson = gson;
        this.scanner = scanner;
    }

    public ClientCommandRequest entryRequest() {
        while (true) {
            System.out.println("Choose an option: ");
            System.out.println("1. login");
            System.out.println("2. register");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1") || choice.equalsIgnoreCase("login")) {
                return buildAuthRequest(CommandType.LOGIN);
            } else if (choice.equals("2") || choice.equalsIgnoreCase("register")) {
                return buildAuthRequest(CommandType.REGISTER);
            } else {
                System.out.println("Incorrect choice! Try again!");
            }
        }
    }

    private ClientCommandRequest buildAuthRequest(CommandType type) {
        while (true) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            if (type == CommandType.REGISTER) {
                System.out.print("Confirm your password: ");
                String confirm = scanner.nextLine();
                if (!password.equals(confirm)) {
                    System.out.println("Passwords are different! Try again");
                    continue;
                }
            }

            CreateUserDto dto = new CreateUserDto(username, password);

            ClientCommandRequest req = new ClientCommandRequest();
            req.setType(type);
            req.setArgs(gson.toJson(dto));
            return req;
        }
    }

    public ClientCommandRequest chatRequestCreator(String token) {
        System.out.println("Commands: /msg <text>, /history, /exit, /change_profile");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            ClientCommandRequest request = new ClientCommandRequest();
            request.setToken(token);

            if (line.equalsIgnoreCase("/exit")) {
                request.setType(CommandType.EXIT);
                return request;
            } else if (line.equalsIgnoreCase("/history")) {
                request.setType(CommandType.HISTORY);
                return request;
            } else if (line.startsWith("/msg ")) {
                String text = line.substring(5).trim();
                if (text.isEmpty()) {
                    System.out.println("Message can't be empty");
                    continue;
                }
                request.setType(CommandType.MSG);
                request.setArgs(text);
                return request;
            } else if (line.equalsIgnoreCase("/change_profile")) {
                request.setType(CommandType.CHANGE_PROFILE);
                return request;
            } else {
                System.out.println("Unknown command. Use /msg, /history, /exit, /change_profile");
            }
        }
    }
}

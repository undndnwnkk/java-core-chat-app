import model.User;
import repository.InMemoryMessageRepository;
import repository.InMemoryUserRepository;
import service.ChatService;
import service.MessageService;
import service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(new InMemoryUserRepository());
        MessageService messageService = new MessageService(new InMemoryMessageRepository(), userService);
        ChatService chatService = new ChatService();

        System.out.println("Welcome to this chat app!");
        System.out.println("Before get started you need to login/register");
        System.out.println("Choose an option: ");
        System.out.println("1. login");
        System.out.println("2. register");
        Scanner scanner = new Scanner(System.in);

        User currentUser = chatService.chooseEntryOption(scanner, scanner.nextLine(), userService);

        chatService.runChatLoop(scanner, currentUser, userService, messageService);
    }
}

import com.google.gson.Gson;
import model.*;
import service.ClientService;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    private static final int PORT = 8080;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        Gson gson = new Gson();
        Scanner scanner = new Scanner(System.in);
        ClientService clientService = new ClientService(gson, scanner);

        try (Socket clientSocket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            System.out.println("=== Добро пожаловать в TCP Chat! ===");

            String token = authenticate(clientService, gson, out, in);
            if (token == null) {
                System.out.println("Не удалось авторизоваться. Выход.");
                return;
            }

            chatLoop(clientService, gson, out, in, token);

        } catch (IOException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
        System.out.println("Соединение закрыто.");
    }

    private static String authenticate(ClientService clientService, Gson gson,
                                       PrintWriter out, BufferedReader in) throws IOException {
        while (true) {
            ClientCommandRequest request = clientService.entryRequest();
            out.println(gson.toJson(request));

            String jsonResponse = in.readLine();
            ServerResponse response = gson.fromJson(jsonResponse, ServerResponse.class);

            if (response.isSuccess() && response.getToken() != null) {
                System.out.println("✅ Успешный вход! Токен получен.");
                return response.getToken();
            } else {
                System.out.println("❌ " + (response.getMessage() != null ? response.getMessage() : "Неизвестная ошибка"));
                System.out.println("Попробуйте снова...");
            }
        }
    }

    private static void chatLoop(ClientService clientService, Gson gson,
                                 PrintWriter out, BufferedReader in, String token) throws IOException {
        Thread readerThread = new Thread(() -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    String jsonResponse = in.readLine();
                    if (jsonResponse == null) break;

                    ServerResponse response = gson.fromJson(jsonResponse, ServerResponse.class);
                    handleChatResponse(response);
                }
            } catch (IOException e) {
                System.out.println("Чтение прервано");
            }
        });

        readerThread.start();

        while (true) {
            ClientCommandRequest request = clientService.chatRequestCreator(token);

            if (request.getType() == CommandType.EXIT) {
                out.println(gson.toJson(request));
                System.out.println("👋 До свидания!");
                break;
            }

            if (request.getType() == CommandType.CHANGE_PROFILE) {
                System.out.println("\n🔄 Смена профиля...");
                String newToken = authenticate(clientService, gson, out, in);
                if (newToken != null) {
                    token = newToken;
                }
                continue;
            }

            out.println(gson.toJson(request));
        }
    }

    private static void handleChatResponse(ServerResponse response) {
        if (!response.isSuccess()) {
            System.out.println("❌ Ошибка: " + (response.getMessage() != null ? response.getMessage() : "Неизвестная ошибка"));
            return;
        }

        if (response.getMessage() != null) {
            System.out.println("📨 " + response.getMessage());
        }

        if (response.getPayload() != null) {
            System.out.println("📜 " + response.getPayload());
        }
    }
}

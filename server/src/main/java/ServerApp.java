import com.google.gson.Gson;
import model.ServerResponse;
import repository.InMemoryMessageRepository;
import repository.InMemoryUserRepository;
import repository.MessageRepository;
import repository.UserRepository;
import service.MessageService;
import service.ServerService;
import service.UserService;
import util.LoggerUtil;
import org.slf4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private static final Logger log = LoggerUtil.SERVER;
    private static final Gson gson = new Gson();
    private static final UserRepository userRepository = new InMemoryUserRepository();
    private static final MessageRepository messageRepository = new InMemoryMessageRepository();
    private static final UserService userService = new UserService(userRepository);
    private static final MessageService messageService = new MessageService(messageRepository, userService);
    private static final ServerService serverService = new ServerService(userRepository, messageRepository);
    private static final CopyOnWriteArrayList<PrintWriter> writers = new CopyOnWriteArrayList<>();
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) {
        log.info("🚀 TCP Chat Server starting on port 8080...");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            log.info("✅ Server ready. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("🔗 New client: {}", clientSocket.getInetAddress());

                executor.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            log.error("💥 Server crashed", e);
        }
    }

    private static void handleClient(Socket socket) {
        Logger clientLog = LoggerUtil.CLIENT_HANDLER;
        clientLog.info("👤 Client {} connected", socket.getInetAddress());

        PrintWriter out = null;
        try (PrintWriter currentOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out = currentOut;
            writers.add(out);

            String jsonRequest;
            while ((jsonRequest = in.readLine()) != null) {
                clientLog.debug("📨 Received: {}", jsonRequest);

                ServerResponse response = serverService.processRequest(gson, jsonRequest, socket.getInetAddress().toString());

                if (response.isBroadcast()) {
                    for (PrintWriter writer : writers) {
                        writer.println(gson.toJson(response));
                    }
                } else {
                    out.println(gson.toJson(response));
                }
                clientLog.debug("📤 Sent: {}", gson.toJson(response));
            }
        } catch (IOException e) {
            clientLog.warn("⚠️ Client {} disconnected", socket.getInetAddress(), e);
        } finally {
            if (out != null) {
                writers.remove(out);
            }
            clientLog.info("👋 Client {} disconnected", socket.getInetAddress());
        }
    }
}

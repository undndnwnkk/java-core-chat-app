import com.google.gson.Gson;
import model.ServerResponse;
import repository.*;
import service.MessageService;
import service.ServerService;
import service.UserService;
import util.LoggerUtil;
import org.slf4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private static final Logger log = LoggerUtil.SERVER;
    private static final Gson gson = new Gson();
    private static final UserRepository userRepository = new JdbcUserRepository();
    private static final MessageRepository messageRepository = new JdbcMessageRepository();
    private static final ServerService serverService = new ServerService(userRepository, messageRepository);
    private static final CopyOnWriteArrayList<PrintWriter> writers = new CopyOnWriteArrayList<>();
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SHUTDOWN]Shutting down...");

            String goodbye = gson.toJson(new ServerResponse(true, null, "Сервер закрывается", null, true));
            for (PrintWriter writer : writers) {
                writer.println(goodbye);
            }

            executor.shutdown();

            System.out.println("[SHUTDOWN] All clients received goodbye messages. Bye!");
        }));

        log.info("🚀 TCP Chat Server starting on port 8080...");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            log.info("✅ Server ready. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("🔗 New client: {}", clientSocket.getInetAddress());

                executor.submit(() -> {
                    handleClient(clientSocket);
                });
            }
        } catch (IOException e) {
            log.error("💥 Server crashed", e);
        }
    }

    private static void handleClient(Socket socket) {
        Logger clientLog = LoggerUtil.CLIENT_HANDLER;
        PrintWriter out = null;

        try (PrintWriter currentOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            socket.setSoTimeout(5 * 60 * 1000);
            out = currentOut;
            writers.add(out);

            clientLog.info("👤 Client {} connected", socket.getInetAddress());

            String jsonRequest;
            while ((jsonRequest = in.readLine()) != null) {
                ServerResponse response = serverService.processRequest(gson, jsonRequest, socket.getInetAddress().toString());

                String jsonResponse = gson.toJson(response);
                if (response.isBroadcast()) {
                    for (PrintWriter writer : writers) {
                        writer.println(jsonResponse);
                    }
                } else {
                    out.println(jsonResponse);
                }
            }
        } catch (java.net.SocketTimeoutException e) {
            clientLog.warn("⌛ Client {} timed out", socket.getInetAddress());
        } catch (IOException e) {
            clientLog.warn("⚠️ Client {} disconnected with error: {}", socket.getInetAddress(), e.getMessage());
        } finally {
            if (out != null) {
                writers.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Error closing socket", e);
            }
            clientLog.info("👋 Client {} cleanup done", socket.getInetAddress());
        }
    }
}

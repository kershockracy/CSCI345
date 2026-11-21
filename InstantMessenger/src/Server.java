
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Server {

    public static void main(String[] args) {

        int port = 6000;

        try (ServerSocket server = new ServerSocket(port)) {

            System.out.println("Server running on port " + port);

            // Keep the server running and accepting clients.
            while (true) {

                // Wait for a new client to connect.
                Socket client = server.accept();

                System.out.println("Client connected: "
                        + client.getRemoteSocketAddress());

                /*
                 * Create a separate thread for each client.
                 *
                 * This allows multiple clients to connect
                 * without waiting for another client to finish.
                 *
                 * The thread runs the handleClient() method.
                 */
                Thread clientThread = new Thread(() -> handleClient(client));

                // Start the new thread.
                clientThread.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    /*
     * This method handles communication with one client.
     *
     * Each connected client gets its own thread running
     * this method.
     */
    private static void handleClient(Socket client) {

        try (Socket socket = client;

                // Receive messages from this client.
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                // Send replies back to this client.
                PrintWriter output = new PrintWriter(
                        socket.getOutputStream(), true)) {

            // Read the username sent by the client.
            String username = input.readLine();

            // Display the username on the server.
            System.out.println("User connected: " + username);

            String message;

            // Keep receiving messages until the client disconnects.
            while ((message = input.readLine()) != null) {

                // Show which user sent the message.
                System.out.println(username + ": " + message);

                // Reply to the client that sent the message.
                output.println("Server received: " + message);
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }

        // This appears when the client disconnects.
        System.out.println("Client disconnected.");
    }
}

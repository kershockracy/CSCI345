
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    /*
     * Store the usernames of connected clients.
     *
     * Each username is associated with a PrintWriter,
     * which the server uses to send messages to that client.
     *
     * ConcurrentHashMap allows multiple client threads
     * to safely access this list at the same time.
     */
    private static ConcurrentHashMap<String, PrintWriter> onlineClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        int port = 6000;

        try (ServerSocket server = new ServerSocket(port)) {

            System.out.println("Server running on port " + port);

            // Keep accepting new clients.
            while (true) {

                Socket client = server.accept();

                System.out.println("Client connected: "
                        + client.getRemoteSocketAddress());

                // Give each client its own thread.
                Thread clientThread = new Thread(() -> handleClient(client));
                clientThread.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Handle messages from one connected client.
    private static void handleClient(Socket client) {

        try (Socket socket = client;

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                PrintWriter output = new PrintWriter(
                        socket.getOutputStream(), true)) {

            // Read the username entered by the client.
            String username = input.readLine();

            if (username == null || username.trim().isEmpty()) {
                output.println("A username is required.");
                return;
            }

            username = username.trim();
            /*
             * Read the password sent by the client.
             *
             * For this school project, we are using two
             * predefined users with the same test password.
             *
             * Alice: username = alice, password = 1234
             * Bob: username = bob, password = 1234
             */
            String password = input.readLine();

            // Check whether the username is one of our two accounts.
            boolean validUsername = username.equals("alice")
                    || username.equals("bob");

            // Check whether the password is correct.
            boolean validPassword = "1234".equals(password);

            // Reject the connection if either value is incorrect.
            if (!validUsername || !validPassword) {

                output.println("Login failed: Incorrect username or password.");

                System.out.println("Failed login attempt: " + username);

                return;
            }

            /*
             * Add the client to our list of online users.
             *
             * putIfAbsent prevents two clients from
             * using the same username simultaneously.
             */
            if (onlineClients.putIfAbsent(username, output) != null) {
                output.println("Username already in use.");
                return;
            }

            System.out.println("User connected: " + username);
            output.println("Welcome " + username + "!");

            try {
                String message;

                // Keep receiving messages from this client.
                while ((message = input.readLine()) != null) {
                    /*
                     * If the user types "users", display everyone
                     * currently connected to the server.
                     *
                     * onlineClients stores the usernames of
                     * all connected users.
                     *
                     * keySet() gets those usernames.
                     */
                    if (message.equalsIgnoreCase("users")) {

                        output.println("Online users: " + onlineClients.keySet());

                        // Skip the normal messaging code.
                        continue;
                    }

                    /*
                     * A message should look like:
                     *
                     * bob: Hello Bob!
                     *
                     * split() separates the recipient's
                     * name from the actual message.
                     */
                    String[] parts = message.split(":", 2);

                    if (parts.length != 2) {
                        output.println("Use: username: message");
                        continue;
                    }

                    String recipient = parts[0].trim();
                    String text = parts[1].trim();

                    if (recipient.isEmpty() || text.isEmpty()) {
                        output.println("Recipient and message are required.");
                        continue;
                    }

                    /*
                     * Look for the recipient in our
                     * list of connected users.
                     */
                    PrintWriter receiver = onlineClients.get(recipient);

                    if (receiver != null) {

                        // Forward the message to the recipient.
                        receiver.println(username + ": " + text);

                        // Confirm that the server forwarded it.
                        output.println("Sent to " + recipient);

                        System.out.println(username + " to "
                                + recipient + ": " + text);

                    } else {

                        // Tell the sender if the user is not online.
                        output.println(recipient + " is not online.");
                    }
                }

            } finally {

                // Remove the user when the connection ends.
                onlineClients.remove(username, output);

                System.out.println(username + " disconnected.");
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}

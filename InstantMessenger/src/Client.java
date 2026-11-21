import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {

        String serverAddress = "localhost";
        int port = 6000;

        // Connect to the server using TCP.
        try (Socket socket = new Socket(serverAddress, port)) {

            System.out.println("Connected to server!");

            // Prepare to send messages to the server.
            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);

            // Prepare to receive messages from the server.
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Scanner lets the user type messages in the terminal.
            Scanner keyboard = new Scanner(System.in);

            // Ask the user to enter a username.
            System.out.print("Enter username: ");
            String username = keyboard.nextLine();

            // Send the username to the server.
            output.println(username);
            // Ask the user to enter a password.
            System.out.print("Enter password: ");
            String password = keyboard.nextLine();

            // Send the password to the server.
            output.println(password);

            /*
             * Wait for the server to check our login.
             *
             * If the username and password are correct,
             * the server sends a welcome message.
             *
             * If the login fails, stop the client program.
             */
            String loginResult = input.readLine();

            if (!("Welcome " + username + "!").equals(loginResult)) {

                System.out.println(loginResult == null
                        ? "Connection closed."
                        : loginResult);

                return;
            }

            // Display the successful login message.
            System.out.println(loginResult);

            /*
             * Create a separate thread to listen for messages.
             *
             * This allows the client to receive messages
             * even when the user is not typing anything.
             */
            Thread receiveThread = new Thread(() -> {

                try {
                    String incomingMessage;

                    // Keep listening for messages from the server.
                    while ((incomingMessage = input.readLine()) != null) {

                        // Display each incoming message.
                        System.out.println("\nServer says: " + incomingMessage);
                    }

                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });

            // Allow the program to exit when the main thread finishes.
            receiveThread.setDaemon(true);

            // Start listening for messages.
            receiveThread.start();

            // Keep sending messages until the user types quit.
            while (true) {

                System.out.print("Enter message (or quit): ");

                String message = keyboard.nextLine();

                // Stop if the user types quit.
                if (message.equalsIgnoreCase("quit")) {
                    break;
                }

                // Send the typed message to the server.
                output.println(message);

            }

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}

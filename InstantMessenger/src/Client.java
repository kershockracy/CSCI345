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

                // Read the server's reply.
                String reply = input.readLine();

                System.out.println("Server says: " + reply);
            }

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}

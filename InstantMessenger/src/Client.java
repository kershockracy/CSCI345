
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {

        String serverAddress = "localhost";
        int port = 6000;

        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connected to server!");

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);

            output.println("Hello from Client!");
            // Prepare the client to receive the server's reply.
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Read the message from the server.
            String reply = input.readLine();

            // Display the server's reply.
            System.out.println("Server says: " + reply);

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}


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

            while (true) {
                Socket client = server.accept();

                System.out.println("Client connected: "
                        + client.getRemoteSocketAddress());

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));

                // Prepare the server to send messages back to the client.
                PrintWriter output = new PrintWriter(
                        client.getOutputStream(), true);

                // Keep reading messages until the client disconnects.
                String message;

                while ((message = input.readLine()) != null) {

                    // Display each message received.
                    System.out.println("Message received: " + message);

                    // Send a reply to the client.
                    output.println("Server received: " + message);
                }
                client.close();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}

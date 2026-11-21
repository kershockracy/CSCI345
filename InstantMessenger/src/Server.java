
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

                String message = input.readLine();

                System.out.println("Message received: " + message);
                // Prepare the server to send a reply to the client.
                PrintWriter output = new PrintWriter(
                        client.getOutputStream(), true);

                // Send a reply through the TCP connection.
                output.println("Server received your message!");

                client.close();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}

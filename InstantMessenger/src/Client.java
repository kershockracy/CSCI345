
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;

public class Client {
    public static void main(String[] args) {

        String serverAddress = "localhost";
        int port = 6000;

        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connected to server!");

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);

            output.println("Hello from Client!");

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}

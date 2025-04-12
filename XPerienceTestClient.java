package xperience;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Test client for the XPerience server
 */
public class XPerienceTestClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java XPerienceTestClient <Server> <Port>");
            System.exit(1);
        }

        String server = args[0];
        int port = Integer.parseInt(args[1]);

        // Test cases
        testEvent(server, port, "Danoke", "02/12/2025", "8pm", "Fusion of Karaoke and Dance");
        testEvent(server, port, "Dona Dance", "02/14/2025", "8pm", "Dance like you don’t care");
        testEvent(server, port, "Dona Dance Danc", "02/14/2025", "9pm", "Light the night");
        testEvent(server, port, "Danoke", "02/12/2025", "8pm", "Fusion of Karaoke and Dance");
        testEvent(server, port, "Dona Dance", "03/14/2025", "8am", "Dance like you don't care");
        testEvent(server, port, "Dona Dance Dance", "03/14/2025", "8pm", "Dance like you don't care");
        testEvent(server, port, "Safety", "02/16/2025", "8pm", "Leave your freinds");


    }

    private static void testEvent(String server, int port, String name, String date, String time, String description) {
        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII);
             Scanner in = new Scanner(socket.getInputStream(), StandardCharsets.US_ASCII)) {

            // Create and send the event message
            String message = name + "#" + date + "#" + time + "#" + description + "#";
            System.out.println("Sending: " + message);
            out.println(message);

            // Read and display the server's response
            String response = in.nextLine();
            System.out.println("Server response: " + response);
            System.out.println();

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}
package edu.seg2105.edu.server.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.seg2105.client.common.ChatIF;

import java.io.IOException;

public class ServerConsole implements ChatIF {
    final private EchoServer server;

    public ServerConsole(EchoServer server) {
        this.server = server;
    }

    // Accept input from the server console
    public void accept() {
        try {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {
                message = fromConsole.readLine();  // Read input from the console
                if (message.startsWith("#")) {
                    handleServerCommand(message);  // Handle commands
                } else {
                    server.sendToAllClients("SERVER MSG> " + message);  // Send to all clients
                    display("SERVER MSG> " + message);  // Display message on the server console
                }
            }
        } catch (IOException e) {
            System.out.println("Unexpected error while reading from server console.");
        }
    }

    // Implementing the display method from the ChatIF interface
    @Override
    public void display(String message) {
        System.out.println(message);  // Display the message to the server console
    }

    // Handle commands that the server console user types
    private void handleServerCommand(String command) {
        if (command.equalsIgnoreCase("#quit")) {
            server.quit();
        } else if (command.equalsIgnoreCase("#stop")) {
            server.stopListening();
        } else if (command.equalsIgnoreCase("#close")) {
            try {
                server.close();  // Close the server and handle any IOExceptions
            } catch (IOException e) {
                System.out.println("Error closing the server: " + e.getMessage());
            }
        } else if (command.startsWith("#setport")) {
            String[] commandParts = command.split(" ");
            if (commandParts.length == 2) {
                try {
                    int port = Integer.parseInt(commandParts[1]);
                    server.setPort(port);
                    System.out.println("Port set to " + port);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number.");
                }
            }
        } else if (command.equalsIgnoreCase("#start")) {
            try {
                server.listen();
            } catch (IOException e) {
                System.out.println("Error starting server.");
            }
        } else if (command.equalsIgnoreCase("#getport")) {
            System.out.println("Current port: " + server.getPort());
        } else {
            System.out.println("Unknown command.");
        }
    }
}

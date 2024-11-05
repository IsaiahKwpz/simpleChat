package edu.seg2105.edu.server.ui;

package edu.seg2105.server.ui;

import java.util.Scanner;
import edu.seg2105.server.backend.EchoServer;
import edu.seg2105.client.common.ChatIF;

/**
 * This class constructs the UI for the server and implements the
 * chat interface for command handling.
 */
public class ServerConsole implements ChatIF {
    final public static int DEFAULT_PORT = 5555;
    EchoServer server;
    Scanner fromConsole;

    public ServerConsole(int port) {
        server = new EchoServer(port);
        fromConsole = new Scanner(System.in);
    }

    /**
     * This method starts the server and waits for console input.
     */
    public void accept() {
        try {
            server.listen();  // Start listening for clients
        } catch (Exception e) {
            System.out.println("Error: Could not start server.");
            return;
        }

        while (true) {
            String message = fromConsole.nextLine();
            handleCommand(message);
        }
    }

    /**
     * This method handles commands from the server console.
     * 
     * @param command The command entered by the server admin.
     */
    private void handleCommand(String command) {
        if (command.equalsIgnoreCase("#quit")) {
            System.exit(0);
        } else if (command.equalsIgnoreCase("#stop")) {
            server.stopListening();
        } else if (command.equalsIgnoreCase("#close")) {
            try {
                server.close();
            } catch (Exception e) {
                System.out.println("Error closing server.");
            }
        } else if (command.startsWith("#setport")) {
            if (!server.isListening()) {
                String[] tokens = command.split(" ");
                if (tokens.length > 1) {
                    try {
                        server.setPort(Integer.parseInt(tokens[1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid port number.");
                    }
                } else {
                    System.out.println("Usage: #setport <port>");
                }
            } else {
                System.out.println("Stop the server before setting the port.");
            }
        } else if (command.equalsIgnoreCase("#start")) {
            if (!server.isListening()) {
                try {
                    server.listen();
                } catch (Exception e) {
                    System.out.println("Could not start server.");
                }
            } else {
                System.out.println("Server is already running.");
            }
        } else if (command.equalsIgnoreCase("#getport")) {
            System.out.println("Current port: " + server.getPort());
        } else {
            System.out.println("Unknown command.");
        }
    }

    @Override
    public void display(String message) {
        System.out.println("SERVER MSG> " + message);
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No port specified. Defaulting to " + DEFAULT_PORT);
        }

        ServerConsole serverConsole = new ServerConsole(port);
        serverConsole.accept();
    }
}

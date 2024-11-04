package edu.seg2105.edu.server.backend;

public class ServerConsole implements ChatIF {
    final private EchoServer server;

    public ServerConsole(EchoServer server) {
        this.server = server;
    }

    public void accept() {
        try {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {
                message = fromConsole.readLine();
                if (message.startsWith("#")) {
                    handleServerCommand(message);
                } else {
                    server.sendToAllClients("SERVER MSG> " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Unexpected error while reading from server console.");
        }
    }

    private void handleServerCommand(String command) {
        if (command.equalsIgnoreCase("#quit")) {
            server.quit();
        } else if (command.equalsIgnoreCase("#stop")) {
            server.stopListening();
        } else if (command.equalsIgnoreCase("#close")) {
            server.close();
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


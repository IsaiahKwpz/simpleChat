package server.ui;


import java.io.IOException;
import java.util.Scanner;
import server.backend.EchoServer;
import client.common.ChatIF;

/**
 * This class constructs the UI for the server and implements the
 * chat interface for command handling.
 */
public class ServerConsole implements ChatIF {
	
	//Instance Variables
	
    final public static int DEFAULT_PORT = 5555;
    EchoServer server;
    Scanner fromConsole;

    /*********************************
     * Costructors
     * @param port
     */
    public ServerConsole(int port) {
    	try {
    		server = new EchoServer(port, this);
    		server.listen();
    	}catch(IOException excpetion) {
    		System.out.println("Error: Could not listen for clients!");
			System.exit(1);
    	}
        
        fromConsole = new Scanner(System.in);
    }
    /********************************
     
    
    /**
     * This method starts the server and waits for console input.
     */
    public void accept() {
    	try {
    		
    		while (true) {
    			String message = fromConsole.nextLine();
    			server.handleMessageFromServerUI(message);
    		}
    	}catch(Exception ex) {
  	      System.out.println("Unexpected error when trying to read from console");
  	    }
    }

    
    /**
     * This method handles displaying messages to Server UI, ovveriding the CatIF display method
     */
    @Override
    public void display(String message) {
        System.out.println("SERVER MSG> " + message);
    }

    //Class Methods ********************************
    /**
     * This method creates the server UI
     */
    public static void main(String[] args) {
        int port = 0;

        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) { //if no port providing, set default port
            System.out.println("No port specified. Defaulting to " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }

        ServerConsole serverConsole = new ServerConsole(port);
        serverConsole.accept();
    }
}

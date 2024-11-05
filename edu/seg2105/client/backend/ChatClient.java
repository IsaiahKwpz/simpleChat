// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
    
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  private String loginID;  // The client's login id

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  if (message.startsWith("#")) {
	        // Handle commands
	        handleCommand(message);
	    } else {
	        try {
	            sendToServer(message);
	        } catch (IOException e) {
	            clientUI.display("Could not send message to server. Terminating client.");
	            quit();
	        }
	    }
  }
  
  /**
   * Sends the login ID to the server when the connection is established.
   */
  @Override
  protected void connectionEstablished() {
      try {
          sendToServer("#login " + loginID);  // Send the login ID to the server
      } catch (IOException e) {
          clientUI.display("Could not send login ID to the server.");
          quit();
      }
  }
  
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {clientUI.display("Error closing connection");}
    System.exit(0);
  }
  
  @Override
  public void connectionClosed() {
      System.out.println("Connection closed. Server has shut down.");
      clientUI.display("The server has shut down.");
      quit();
  }

  @Override
  public void connectionException(Exception exception) {
      System.out.println("Server has shut down unexpectedly.");
      clientUI.display("The server has shut down unexpectedly.");
      quit();
  }
  /**
   * This method handles commands entered by the client.
   * 
   * @param command The command entered by the client.
   */
  private void handleCommand(String command) {
    if (command.equalsIgnoreCase("#quit")) {
        quit();
    } else if (command.equalsIgnoreCase("#logoff")) {
        try {
            closeConnection();
        } catch (IOException e) {
            clientUI.display("Error logging off.");
        }
    } else if (command.startsWith("#sethost")) {
        if (!isConnected()) {
            String[] tokens = command.split(" ");
            if (tokens.length > 1) {
                setHost(tokens[1]);
            } else {
                clientUI.display("Usage: #sethost <host>");
            }
        } else {
            clientUI.display("You must log off before setting the host.");
        }
    } else if (command.startsWith("#setport")) {
        if (!isConnected()) {
            String[] tokens = command.split(" ");
            if (tokens.length > 1) {
                try {
                    setPort(Integer.parseInt(tokens[1]));
                } catch (NumberFormatException e) {
                    clientUI.display("Invalid port number.");
                }
            } else {
                clientUI.display("Usage: #setport <port>");
            }
        } else {
            clientUI.display("You must log off before setting the port.");
        }
    } else if (command.equalsIgnoreCase("#login")) {
        if (!isConnected()) {
            try {
                openConnection();
            } catch (IOException e) {
                clientUI.display("Could not connect to server.");
            }
        } else {
            clientUI.display("You are already logged in.");
        }
    } else if (command.equalsIgnoreCase("#gethost")) {
        clientUI.display("Current host: " + getHost());
    } else if (command.equalsIgnoreCase("#getport")) {
        clientUI.display("Current port: " + getPort());
    } else {
        clientUI.display("Unknown command.");
    }
  }
  
}
//End of ChatClient class

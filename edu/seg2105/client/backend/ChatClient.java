// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client.backend;

import ocsf.client.*;

import java.io.*;

import client.common.ChatIF;
import client.common.*;

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
	  try {
		  if (message.startsWith("#")) {
			  // Handle commands
	          handleCommand(message);
	    } else if(isConnected()) {
	            sendToServer(message);
	    } 
	  }
	  catch (IOException e) {
	            clientUI.display("Could not send message to server. Terminating client.");
	            quit();
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
  
  /**
   * This method displays connection closed to client when connecton is closed
   */
  
  @Override
  public void connectionClosed() {
      System.out.println("Connection closed");
      clientUI.display("Connection closed");
  }

  /**
   * This method displays server shut down unexepectedly when a connection exception occurs
   */
  @Override
  public void connectionException(Exception exception) {
      System.out.println("Server has shut down unexpectedly.");
      clientUI.display("The server has shut down unexpectedly.");
      System.exit(0);
  }
  /**
   * This method handles commands entered by the client.
   * 
   * @param command The command entered by the client.
   */
  private void handleCommand(String command) {
    if (command.equalsIgnoreCase("#quit")) { //Quits connection and terminates
        quit();
    } 
    else if (command.equals("#logoff")) { //closes connection, but does not terminate
        try {
            closeConnection();
        } catch (IOException e) {
            clientUI.display("Error logging off.");
        }
    } 
    else if (command.startsWith("#sethost")) { //sets host
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
    } 
    else if (command.startsWith("#setport")) { //sets port
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
    } 
    else if (command.equals("#login")) { //logs in
        if (!isConnected()) {
            connectionEstablished();
        } else {
            clientUI.display("You are already logged in.");
        }
    } 
    else if (command.equals("#gethost")) {  //gets host
        clientUI.display("Current host: " + getHost());
    } 
    else if (command.equals("#getport")) {  //gets port
        clientUI.display("Current port: " + getPort());
    } 
    else {
        clientUI.display("Unknown command.");
    }
  }
  
}
//End of ChatClient class

package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
	  String message = msg.toString();

      // Check if the message is a login command
      if (message.startsWith("#login")) {
          String[] parts = message.split(" ");
          if (parts.length == 2) {
              String loginID = parts[1];  // Extract the login ID
              client.setInfo("loginID", loginID);  // Store the login ID in the client connection
              System.out.println("Client " + loginID + " has logged in.");
          } else {
              try {
                  client.sendToClient("Error: Invalid login command.");
                  client.close();  // Close connection if the login command is invalid
              } catch (IOException e) {
                  System.out.println("Error handling invalid login.");
              }
          }
      } else {
          // Check if the client has already logged in
          if (client.getInfo("loginID") == null) {
              try {
                  client.sendToClient("Error: You must log in first.");
                  client.close();  // Close connection if the login has not occurred
              } catch (IOException e) {
                  System.out.println("Error: Unable to close connection.");
              }
          } else {
              // Handle normal messages and prefix with loginID
              String loginID = (String) client.getInfo("loginID");
              System.out.println("Message from " + loginID + ": " + message);
              sendToAllClients(loginID + ": " + message);  // Broadcast the message with loginID
          }
      }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  @Override
  protected void clientConnected(ConnectionToClient client) {
      System.out.println("Client connected: " + client);
  }

  @Override
  protected void clientDisconnected(ConnectionToClient client) {
      System.out.println("Client disconnected: " + client);
  }
  
  public void quit() {
	    try {
	        close();
	    } catch (IOException e) {
	        System.out.println("Error closing server.");
	    }
	    System.exit(0);
  }

  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class

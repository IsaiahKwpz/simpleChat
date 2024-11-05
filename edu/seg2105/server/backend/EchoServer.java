package server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import client.common.ChatIF;
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
  
  final public static String loginKey = "loginID";
  
  ChatIF serverUI; 
  
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
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  System.out.println("Message received: " + msg + " from " + client.getInfo(loginKey));
	  String message = msg.toString();

      if (message.startsWith("#login ")) {
          if (client.getInfo("loginID") == null) {
        	  String loginID = message.substring(7);
        	  client.setInfo(loginKey, loginID);
        	  
        	  //Send login message
        	  String newMessage = loginID + " has logged on";
        	  System.out.println(newMessage);
  			  sendToAllClients(newMessage);
          }
          else {
        	  try {
        		  client.sendToClient("Error - Already logged in - terminating connection");
  				client.close();
  			} catch (IOException e) {}        	  
         }
      }
      else {
    	  Object loginID = client.getInfo(loginKey);
    	  this.sendToAllClients(loginID + "> " + msg);
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
  /**
   * Called when a client connects to the server.
   * 
   * @param client The client that connected.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
      System.out.println("Client connected: " + client);
  }
  /**
   * Called when a client disconnects from the server.
   * 
   * @param client The client that disconnected.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
      System.out.println("Client disconnected: " + client);
  }
  /**
   * Called when a client is disconnected due to an exception.
   * 
   * @param client The client that was disconnected.
   * @param exception The exception that caused the disconnection.
   */
  @Override
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
      System.out.println("Client disconnected unexpectedly: " + client);
  }
  
  /**
   * 
   * @param this method will handle data coming from server console
   */
  public void handleMessageFromServerUI(String message) {
	    if(message.startsWith("#")) {
			handleCommand(message);
		}
		
		else if(isListening()){
			// echo server message to the server and all clients
			String toDisplay = "Server MSG> " + message;
			serverUI.display(toDisplay);
			sendToAllClients(toDisplay);
		}
}
  
  /**
   * This method handles commands from the server console.
   * 
   * @param command The command entered by the server admin.
   */
  private void handleCommand(String command) {
      if (command.equals("#quit")) {
          quit();
      } else if (command.equals("#stop")) {
          stopListening();
      } else if (command.equals("#close")) {
          try {
              close();
          } catch (Exception e) {
              System.out.println("Error closing server.");
          }
      } else if (command.startsWith("#setport")) {
          if (getNumberOfClients() == 0 && !isListening()) {
              String[] tokens = command.split(" ");
              if (tokens.length > 1) {
                  try {
                      setPort(Integer.parseInt(tokens[1]));
                  } catch (NumberFormatException e) {
                      System.out.println("Invalid port number.");
                  }
              } else {
                  System.out.println("Usage: #setport <port>");
              }
          } else {
              System.out.println("Stop the server before setting the port.");
          }
      } else if (command.equals("#start")) {
          if (!isListening()) {
              try {
                  listen();
              } catch (Exception e) {
                  System.out.println("Could not start server.");
              }
          } else {
              System.out.println("Server is already running.");
          }
      } else if (command.equalsIgnoreCase("#getport")) {
          System.out.println("Current port: " + getPort());
      } else {
          System.out.println("Unknown command.");
      }
  }
  
  
  /**
   * This method terminates the server.
   */
  public void quit()
  {
    try
    {
      close();
    }
    catch(IOException e) {}
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

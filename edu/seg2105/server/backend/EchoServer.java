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
  /**
   * LoginKey to get client info
   */
  final public static String loginKey = "loginID";
  
  //Instance Variable 
  ChatIF server; 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF server){
    super(port);
    this.server = server;
  
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client){
	  
	  server.display("Message received: " + msg + " from " + client.getInfo(loginKey));
	  String message = msg.toString();

      if (message.startsWith("#login ")) {
          if (client.getInfo("loginID") == null) {
        	  String loginID = message.substring(7);
        	  client.setInfo(loginKey, loginID);
        	  
        	  //Send login message
        	  String sentMsg = loginID + " has logged on";
        	  server.display(sentMsg);
  			  sendToAllClients(sentMsg);
          }
          else {
        	  try {
        		client.sendToClient("Already logged in, terminating connection");
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
  protected void serverStarted(){
    server.display("Server is now listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    server.display("Server has now stopped listening for connections.");
  }
  
  
  /*****Hook Methods******
  
  
   */
  /**
   * Called when a client connects to the server.
   * 
   * @param client The client that connected.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  String loginID = (String) client.getInfo(loginKey);
      server.display("Client connected: " + loginID);
  }
  /**
   * Called when a client disconnects from the server.
   * 
   * @param client The client that disconnected.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  String loginID = (String) client.getInfo(loginKey);
      server.display("Client disconnected: " + loginID);
  }
  /**
   * Called when a client is disconnected due to an exception.
   * 
   * @param client The client that was disconnected.
   * @param exception The exception that caused the disconnection.
   */
  @Override
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  String loginID = (String) client.getInfo(loginKey);
      server.display("Client disconnected unexpectedly: " + loginID);
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
			server.display(toDisplay);
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
      } 
      else if (command.equals("#stop")) {
          stopListening();
      } 
      else if (command.equals("#close")) {
          try {
              close();
          } catch (Exception e) {
              System.out.println("Error closing server.");
          }
      } 
      else if (command.startsWith("#setport")) {
          if (getNumberOfClients() == 0 && !isListening()) {
              String[] tokens = command.split(" ");
              if (tokens.length > 1) {
                  try {
                      setPort(Integer.parseInt(tokens[1]));
                  } catch (NumberFormatException e) {
                      System.out.println("Invalid port number.");
                  }
              } else {
                  System.out.println("Type: #setport <port>");
              }
          } else {
              System.out.println("Stop the server before setting the port.");
          }
      } 
      else if (command.equals("#start")) {
          if (!isListening()) {
              try {
                  listen();
              } catch (Exception e) {
                  System.out.println("Could not lsiten for clients.");
              }
          } else {
              System.out.println("Server is already running.");
          }
      } 
      else if (command.equalsIgnoreCase("#getport")) {
          System.out.println("Current port: " + getPort());
      } 
      else {
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
  
  
}
//End of EchoServer class

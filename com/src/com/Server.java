/**
* Server Class
* Implements routines to create server socket and routines to listen for new connections.
* Implements routine to register and disconnect users and maintains information about connected users.
**/
package com;

import java.io.*;
import java.net.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

	public static final String COMMAND_WHOAMI = "whoami";
	public static final String COMMAND_LIST = "list";
	public static final String COMMAND_RELAY = "relay";
	public static final String COMMAND_BROADCAST = "broadcast";
	public static final String COMMAND_BYE = "bye";

	public static final int PORT = 8484; 

	// Server Socket the server would listen on.
	private static ServerSocket serverSocket; 
	// allTimeTotalUsers will be incremented and used as unique ID
	private static long allTimeTotalUsers = 0;

	// List of active users
	private static List<Long> activeUsers;
	// Map of message queue to each of the registered client
	private static HashMap<Long, BlockingQueue<String>> messageQueueMap;
	// Map of connection instance for each client
	private static HashMap<Long, ClientConnection> clientConnectionMap;
	// Message relay service used to queue and deliver messages.
	private static RelayService relayService;

	/**
	* long getNewUserID()
	* Returns a unique long int used as identifier for the client.
	* Uses synchronized block to make sure unique ids are handed off.
	**/
	public static long getNewUserID() {
		long currentID;
		synchronized(Server.class) {
			Server.allTimeTotalUsers += 1;
			currentID = Server.allTimeTotalUsers;
		}	
		return currentID;
	}


	/**
	* void registerNewUser(long clientID)
	* Creates a blocking message queue that will be used for sending messages to this client.
	* Also creates a connection instance for the client.
	**/
	public static ClientConnection registerNewUser(long clientID, Socket socket) {
		Server.activeUsers.add(clientID);
		BlockingQueue<String> newMessageQueue = new LinkedBlockingQueue<String>(10);
		Server.messageQueueMap.put(clientID, newMessageQueue);
		ClientConnection connInstance = new ClientConnection(clientID, socket, newMessageQueue);
		Server.clientConnectionMap.put(clientID, connInstance);
		return connInstance;
	}

	/**
	* void disconnectUser(long)
	* cleanup procedure when a client disconnects. Release client connection and remove from active users list.
	**/
	public static void disconnectUser(long clientID) {
		if (Server.messageQueueMap.containsKey(clientID)) {
			Server.messageQueueMap.remove(clientID);
		}
		if (Server.clientConnectionMap.containsKey(clientID)) {
			Server.clientConnectionMap.get(clientID).disconnectConnection();
			Server.clientConnectionMap.remove(clientID);
		}
		Server.activeUsers.remove(clientID);
		return;
	}

	/**
	* List getActiveUsers()
	* Returns list of all current active users.
	**/
	public static List<Long> getActiveUsers() {
		return Server.activeUsers;
	}

	/**
	* boolean placeRelayRequest(Message)
	* Queues a given message to be relayed. The message object has the recipient and message body information. 
	**/
	public static boolean placeRelayRequest(Message msgObject) {
		return Server.relayService.placeRequest(msgObject);
	}

	/**
	* ServerSocket getServerSocket()
	**/
	public static ServerSocket getServerSocket(int portNumber) {
		ServerSocket sSocket = null;
		try {
			sSocket = new ServerSocket(portNumber);
		} catch(IOException ioe) {
			System.out.println("Caught IOException while creating ServerSocket");
			System.exit(0);
		}

		return sSocket;
	}

	public static void serverInit() {
		Server.activeUsers = new ArrayList<Long>();
		Server.messageQueueMap = new HashMap<Long, BlockingQueue<String>>();
		Server.clientConnectionMap = new HashMap<Long, ClientConnection>();
		Server.relayService = new RelayService(messageQueueMap);
		Server.relayService.start();
	}

	public static void main(String args[]) {
		Socket incomingNewSocket;
		long clientID;

		try {
			Server.serverSocket = Server.getServerSocket(Server.PORT);
			if (Server.serverSocket == null) {
				System.out.println("Error creating server socket. Exiting.");
				System.exit(0);
			} 
			Server.serverInit();

			System.out.println("Listening on port " + Server.PORT + ". Waiting for clients ... ");
			while (true) {

				incomingNewSocket = Server.serverSocket.accept();

				clientID = getNewUserID();
				System.out.println("Got a connection! Assigned Client ID: " + clientID);
				ClientConnection connInstance = Server.registerNewUser(clientID, incomingNewSocket);
				// Connection Instance takes care of messages coming from the client. Start connection instance. 
				connInstance.start();
			}

		} catch(Exception e) {
			System.out.println("Exception : " + e);
		}

	}
}
package src;

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

	private static long allTimeTotalUsers = 0;
	private static List<Long> activeUsers;
	private static HashMap<Long, BlockingQueue<String>> messageQueueMap;


	public static long getNewUserID() {
		// synchronize this
		Server.allTimeTotalUsers += 1;
		long currentID = Server.allTimeTotalUsers;
		Server.activeUsers.add(currentID);
		return currentID;
	}

	public static List<Long> getActiveUsers() {
		System.out.println("Got request for active users " + Server.activeUsers.toString());
		return Server.activeUsers;
	}

	public static boolean relayMessage(long recipientID, String msgBody) {

		try {
			System.out.println("Relay Message : " + msgBody);
			if (Server.messageQueueMap.containsKey(recipientID)){
				BlockingQueue<String> msgQueue = Server.messageQueueMap.get(recipientID);
				msgQueue.put(msgBody);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Caught : " + e);
			return false;
		}
	}

	public static void main(String args[]) {
		String inputMsg = null;
		Socket incomingSocket;

		long clientID;

		try {
			ServerSocket socket = new ServerSocket(8383);

			Server.activeUsers = new ArrayList<Long>();
			Server.messageQueueMap = new HashMap<Long, BlockingQueue<String>>(); 
			System.out.println("Listening on port 8383. Waiting for clients ... ");

			while (true) {
				// Listen for connection on port 8383.
				incomingSocket = socket.accept();
				System.out.println("Got a connection!");

				clientID = getNewUserID();
				BlockingQueue<String> newMessageQueue = new LinkedBlockingQueue<String>(10);
				Server.messageQueueMap.put(clientID, newMessageQueue);
				ClientConnection connInstance = new ClientConnection(clientID, incomingSocket, newMessageQueue);
				System.out.println("ClientID : " + clientID);
				connInstance.start();
				System.out.println("Handed off.");
			}

			//Receiver receiver = new Receiver(incomingSocket);
			//receiver.start();	
			// DataOutputStream outToClient = new DataOutputStream(incomingSocket.getOutputStream());	
			// BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));

			// DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// System.out.print("[" + dateFormat.format(new Date()) + "]:");
			// while((inputMsg=brIn.readLine())!=null) {
			// 	System.out.print("[" + dateFormat.format(new Date()) + "]:");
			// 	outToClient.writeBytes(inputMsg + '\n');
			// 	outToClient.flush();
			// 	if (inputMsg.equalsIgnoreCase("Bye")) {
			// 		//Close the socket and finish.
			// 		System.out.println("Closing connection.");
			// 		incomingSocket.close();
			// 		break;
			// 	}

			// }

		} catch(Exception e) {
			System.out.println("Exception : " + e);
		}

	}
}
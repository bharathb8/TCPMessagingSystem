/**
* CommandReceiver Class
* This class listens to incoming messages from a client and parses them
* for recognised commands. If the message is not one of the recognized
* commands, then it simple drops them.
**/
package com;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class CommandReceiver extends Thread {
	//client socket connection and the clientID that it belongs to.
	protected Socket connSocket;
	protected Long clientID;

	public CommandReceiver(Socket s, long id) {
		this.connSocket = s;
		this.clientID = id;
	}

	/**
	* String getCSVString(List)
	* Given list of client IDs return a CSV string of their clientIDs.
	* This method makes sure it excludes the ID of the requesting client.
	**/
	public String getCSVString(List<Long> nums) {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (ListIterator<Long> iter = nums.listIterator(); iter.hasNext();) {
			Long userId = iter.next();
			if (userId == this.clientID) {
				continue;
			}
			sb.append(prefix);
			prefix = ",";
			sb.append(userId);
		}
		return sb.toString();
	}

	/**
	* Message parseMessage(String)
	* Given a string message this method evaluates if the message is in the format:
	* relay <list of clients> body: <message body>
	* A Message object is prepared and returned if the string format is correct else
	* null object is returned.
	**/
	public Message parseMessage(String message) {
		Message msgObject = null;

		String[] words = message.split("\\s+");
		if (words.length>0 && words[0].equalsIgnoreCase(Server.COMMAND_RELAY)) {
			String[] recipientsStr = words[1].split(",");
			List<Long> recipientsList = new ArrayList<Long>();
			for (int i=0; i<recipientsStr.length; i++) {
				try {
					Long num = Long.valueOf(recipientsStr[i]).longValue();
					recipientsList.add(num);
				} catch (NumberFormatException nfe) {
					System.out.println("NumberFormatException trying to convert:" + recipientsStr[i]);
				}
			}

			String msgBody = "";
			int index = message.indexOf(":");
			if (index > 0 && index < message.length() - 2) {
				msgBody = message.substring(index+1);
			}

			msgObject = new Message(this.clientID, recipientsList, msgBody);
		}

		return msgObject;
	}

	/**
	* This method polls for message on the client socket and parses it for a recognised command.
	**/
	public void run() {
		String incomingMsg = null;
		BufferedReader reader = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while((incomingMsg = reader.readLine())!= null) {
				if (incomingMsg.equalsIgnoreCase(Server.COMMAND_WHOAMI)) {
					List<Long> recipientsList = new ArrayList<Long>();
					recipientsList.add(this.clientID);

					Server.placeRelayRequest(new Message(0, recipientsList, Long.toString(this.clientID)));
				}
				else if (incomingMsg.equalsIgnoreCase(Server.COMMAND_LIST)) {
					List<Long> activeUsers = Server.getActiveUsers();
					String usersList = getCSVString(activeUsers);
					List<Long> recipientsList = new ArrayList<Long>();
					recipientsList.add(this.clientID);

					Server.placeRelayRequest(new Message(0, recipientsList, usersList));
				} else if (incomingMsg.equalsIgnoreCase(Server.COMMAND_BYE)) {
					break;
				} else {
					Message msgObject = parseMessage(incomingMsg);
					if (msgObject != null) {
						Server.placeRelayRequest(msgObject);
					}
				}
				//System.out.println(incomingMsg);
				//System.out.print("[" + dateFormat.format(new Date()) + "]:");
			}

			// We break out of the while loop if we received the disconnect "Bye" message from user.
			System.out.println("Client:" + this.clientID + " has disconnected.");
			Server.disconnectUser(this.clientID);
			return;

		} catch (Exception e) {
			System.out.println("Caught : " + e);
		}
	}
}

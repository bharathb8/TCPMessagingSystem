package src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class ServerReceiver extends Thread {
	protected Socket connSocket;
	protected Long selfID;

	public ServerReceiver(Socket s, long id) {
		this.connSocket = s;
		this.selfID = id;
	}

	public String getCSVString(List<Long> nums) {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (ListIterator<Long> iter = nums.listIterator(); iter.hasNext();) {
			Long userId = iter.next();
			if (userId == this.selfID) {
				continue;
			}
			sb.append(prefix);
			prefix = ",";
			sb.append(userId);
		}
		return sb.toString();
	}

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

			msgObject = new Message(this.selfID, recipientsList, msgBody);
		}

		return msgObject;
	}

	public void run() {
		String incomingMsg = null;
		BufferedReader reader = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while((incomingMsg = reader.readLine())!= null) {
				if (incomingMsg.equalsIgnoreCase("whoami")) {
					//Server.relayMessage(this.selfID, Long.toString(this.selfID));

					List<Long> recipientsList = new ArrayList<Long>();
					recipientsList.add(this.selfID);
					Message msgObject = new Message(0, recipientsList, Long.toString(this.selfID));
					Server.placeRelayRequest(msgObject);
				}
				else if (incomingMsg.equalsIgnoreCase("list")) {
					List<Long> activeUsers = Server.getActiveUsers();
					String usersList = getCSVString(activeUsers);
					List<Long> recipientsList = new ArrayList<Long>();
					recipientsList.add(this.selfID);
					Message msgObject = new Message(this.selfID, recipientsList, usersList);
					Server.placeRelayRequest(msgObject);
				} else if (incomingMsg.equalsIgnoreCase(Server.COMMAND_BYE)) {
					break;
				} else {
					Message msgObject = parseMessage(incomingMsg);
					if (msgObject != null) {
						Server.placeRelayRequest(msgObject);
					}
				}

				System.out.println(incomingMsg);
				System.out.print("[" + dateFormat.format(new Date()) + "]:");
			}

			// Client wants to disconnect
			System.out.println("Received bye.. so disconnecting user");
			Server.disconnectUser(this.selfID);
			return;

		} catch (Exception e) {
			System.out.println("Caught : " + e);
		}
	}
}

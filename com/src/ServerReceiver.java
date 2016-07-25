package src;

import java.io.*;
import java.net.*;
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

	public void run() {
		String incomingMsg = null;
		BufferedReader reader = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while((incomingMsg = reader.readLine())!= null) {

				if (incomingMsg.equalsIgnoreCase("list")) {
					System.out.println("Here inside list processor");
					List<Long> activeUsers = Server.getActiveUsers();
					String usersList = getCSVString(activeUsers);
					Server.relayMessage(this.selfID, usersList);
				}
				System.out.println(incomingMsg);
				System.out.print("[" + dateFormat.format(new Date()) + "]:");
			}

		} catch (Exception e) {
			System.out.println("Caught : " + e);
		}
	}
}

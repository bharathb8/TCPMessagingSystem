package src;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Receiver extends Thread {
	protected Socket connSocket;

	public Receiver(Socket s) {
		this.connSocket = s;
	}

	public void run() {
		String incomingMsg = null;
		BufferedReader reader = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while((incomingMsg = reader.readLine())!= null) {
				System.out.println(incomingMsg);
				System.out.print("[" + dateFormat.format(new Date()) + "]:");
			}

		} catch (Exception e) {
			System.out.println("Caught : " + e);
		}
	}
}

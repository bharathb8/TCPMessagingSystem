package src;

import java.io.*;
import java.net.*;


public class Receiver extends Thread {
	protected Socket connSocket;

	public Receiver(Socket s) {
		this.connSocket = s;
	}

	public void run() {
		String incomingMsg = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while((incomingMsg = reader.readLine())!= null) {
				System.out.println(incomingMsg);
			}

		} catch (Exception e) {
			System.out.println("Caught : " + e);
		}
	}
}

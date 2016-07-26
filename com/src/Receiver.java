package src;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Receiver extends Thread {
	protected Socket connSocket;
	private String incomingMsg = null;
	private	BufferedReader reader = null;

	public Receiver(Socket s) {
		this.connSocket = s;
		BufferedReader reader = null;
	}

	public void run() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while(!Thread.currentThread().isInterrupted() && (incomingMsg = reader.readLine())!= null) {
				System.out.println(incomingMsg);
				System.out.print("[" + dateFormat.format(new Date()) + "]:");
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("isInterrupted");
					reader.close();
					break;
				}
			}
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("isInterrupted");
			}
			System.out.println("done. out of while...");

		} catch (SocketException e) {
			System.out.println("[ClientReceiver] SE Caught : " + e);
		} catch (Exception e) {
			System.out.println("[ClientReceiver] Caught : " + e);
		}
	}

}

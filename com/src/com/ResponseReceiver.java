/**
* ResponseReceiver Class
* Listens to the inputstream of the client's socket.
* Reads the response from the server and prints it out.
**/
package com;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class ResponseReceiver extends Thread {

	protected Socket connSocket;
	private String incomingMsg = null;
	private	BufferedReader reader = null;

	public ResponseReceiver(Socket s) {
		this.connSocket = s;
		BufferedReader reader = null;
	}

	// Reads input from the socket inputstream and prints it out on to the screen.
	public void run() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while(!Thread.currentThread().isInterrupted() && (incomingMsg = reader.readLine())!= null) {
				System.out.println(incomingMsg);
				System.out.print("[" + dateFormat.format(new Date()) + "]:");
				if (Thread.currentThread().isInterrupted()) {
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Socket closed. Bye!");
			return;
		}
	}

}

/**
* Client Class
* This class makes a connection to the server.
* Takes keyboard input from the user and sends it to the server and displays
* server response.
**/
package com;

import java.io.*;
import java.net.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Client {


	private static Socket connectionSocket;
	private static DataOutputStream outstreamToServer;
	
	public static void main(String[] args) {
		String host = "127.0.0.1";
		if (args.length == 1) {
			host = args[0];
		} else if (args.length > 1) {
			System.out.println("Usage: java Client [server IP]");
			System.out.println("If server IP is not provided, then Localhost is used.");
			System.exit(0);
		}

		String message = null;
		String keyInput = null;
		try {	
			//Connect to Localhost, port 8383
			System.out.println("Connecting to " + host);
			Client.connectionSocket = new Socket(host, 8484);
			System.out.println("Connected to " + host + " . Now write a msg:");

			ResponseReceiver receiver = new ResponseReceiver(Client.connectionSocket);
			receiver.start();

			BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
			Client.outstreamToServer = new DataOutputStream(connectionSocket.getOutputStream());
						
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.print("[" + dateFormat.format(new Date()) + "]:");
			while ((keyInput = brIn.readLine()) != null ) {
				System.out.print("[" + dateFormat.format(new Date()) + "]:");
				Client.outstreamToServer.writeBytes(keyInput + '\n');
				Client.outstreamToServer.flush();

				if (keyInput.equalsIgnoreCase("Bye")) {
					receiver.interrupt();
					connectionSocket.close();
					break;
				}
			}
			
		} catch(Exception e) {
			System.out.println("Caught Exception : " + e);
		} 
	}
}
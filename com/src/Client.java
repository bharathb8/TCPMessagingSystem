package src;

import java.io.*;
import java.net.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Client {

	//private DataOutputStream
	public boolean sendMessage(String msg) {
		return false;
	}
	
	public static void main(String[] args) {
		String message = null;
		String keyInput = null;

		try {
			
			//Connect to Localhost, port 8383
			Socket connectionSocket = new Socket("127.0.0.1", 8383);
			System.out.println("Connected to 127.0.0.1. Now write a msg:");

			Receiver receiver = new Receiver(connectionSocket);
			receiver.start();
			BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
			DataOutputStream outstreamToServer = new DataOutputStream(connectionSocket.getOutputStream());
			BufferedReader instreamFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.print("[" + dateFormat.format(new Date()) + "]:");
			while ((keyInput = brIn.readLine()) != null ) {
				System.out.print("[" + dateFormat.format(new Date()) + "]:");
				outstreamToServer.writeBytes(keyInput + '\n');
				outstreamToServer.flush();

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
package src;

import java.io.*;
import java.net.*;

public class Server {

	public static void main(String args[]) {
		String inputMsg = null;
		Socket incomingSocket;

		try {
			ServerSocket socket = new ServerSocket(8383);
			System.out.println("Listening on port 8383. Waiting for clients ... ");
			// Listen for connection on port 8383.
			incomingSocket = socket.accept();
			System.out.println("Got a connection!");

			Receiver receiver = new Receiver(incomingSocket);
			receiver.start();	
			DataOutputStream outToClient = new DataOutputStream(incomingSocket.getOutputStream());	
			BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));

			while((inputMsg=brIn.readLine())!=null) {
				outToClient.writeBytes(inputMsg + '\n');
				outToClient.flush();
				if (inputMsg.equalsIgnoreCase("Bye")) {
					//Close the socket and finish.
					System.out.println("Closing connection.");
					incomingSocket.close();
					break;
				}

			}

		} catch(Exception e) {
			System.out.println("Exception : " + e);
		}

	}
}
import java.io.*;
import java.net.*;

public class Client {

	public static class Receiver extends Thread {
		protected Socket connSocket;

		public Receiver(Socket s) {
			this.connSocket = s;
		}

		public void run() {
			String incomingMsg = null;
			BufferedReader readerFromServer;
			try {
				readerFromServer = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
				while((incomingMsg = readerFromServer.readLine())!= null) {
					System.out.println(incomingMsg);
				}

			} catch (Exception e) {
				System.out.println("Caught : " + e);
			} finally {
				if (readerFromServer != null) {
					readerFromServer.close();
				}
			}

		}
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

			while ((keyInput = brIn.readLine()) != null ) {		
				outstreamToServer.writeBytes(keyInput + '\n');
				outstreamToServer.flush();

				if (keyInput.equalsIgnoreCase("Bye")) {
					connectionSocket.close();
					break;
				}
			}
			
			
		} catch(Exception e) {
			System.out.println("Caught Exception : " + e);
		} 
	}
}
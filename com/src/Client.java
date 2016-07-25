import java.io.*;
import java.net.*;

public class Client {
	
	public static void main(String[] args) {
		String message = null;
		String keyInput = null;

		try {
			
			//Connect to Localhost, port 8383
			Socket connectionSocket = new Socket("127.0.0.1", 8383);
			System.out.println("Connected to 127.0.0.1. Now write a msg:");
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
				message = instreamFromServer.readLine();
				System.out.println("Server said: " + message);
			}
			
			
		} catch(Exception e) {
			System.out.println("Caught Exception : " + e);
		} 
	}
}
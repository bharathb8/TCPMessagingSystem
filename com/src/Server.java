import java.io.*;
import java.net.*;

public class Server {

	public static void main(String args[]) {
		String clientMsg = null;
		Socket incomingSocket;

		try {
			ServerSocket socket = new ServerSocket(8383);
			System.out.println("Listening on port 8383. Waiting for clients ... ");
			// Listen for connection on port 8383.
			incomingSocket = socket.accept();
			System.out.println("Got a connection!");
	
			BufferedReader readerFromClient = new BufferedReader(new InputStreamReader(incomingSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(incomingSocket.getOutputStream());	
			BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));

			while((clientMsg=readerFromClient.readLine())!=null) {
				System.out.println("Got: " + clientMsg);
				if (clientMsg.equalsIgnoreCase("Bye")) {
					//Close the socket and finish.
					System.out.println("Closing connection.");
					incomingSocket.close();
					break;
				}
				//Send back the same text to client in uppercase
				outToClient.writeBytes(clientMsg.toUpperCase() + '\n');
				outToClient.flush();
			}

		} catch(Exception e) {
			System.out.println("Exception : " + e);
		}

	}
}
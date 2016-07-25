package src;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ClientConnection {

	public class DispatchMessage extends Thread {

		protected BlockingQueue<String> mQueue;
		protected Socket connSocket;

		public DispatchMessage(Socket s, BlockingQueue<String> q) {
			this.connSocket = s;
			this.mQueue = q;
		}

		public void run() {
			try {
				DataOutputStream outToClient = new DataOutputStream(this.connSocket.getOutputStream());
				String message;
				while(true) {
					message = (String)this.mQueue.take();
					outToClient.writeBytes(message + "\n");
				}
			} catch (Exception e) {
				System.out.println("Caught : " + e);
			}
		}
	}

	protected long selfID;
	protected Socket connectionSocket;
	protected BlockingQueue<String> messageQueue;

	public ClientConnection(long id, Socket s, BlockingQueue<String> mQ) {

		this.selfID = id;
		this.connectionSocket = s;
		this.messageQueue = mQ;
		//Sender(this.connectionSocket);
		//Sender.start();
	}

	public void start() {
		ServerReceiver r = new ServerReceiver(this.connectionSocket, this.selfID);
		r.start();
		DispatchMessage dispatcher = new DispatchMessage(this.connectionSocket, this.messageQueue);
		dispatcher.start();
	}

}
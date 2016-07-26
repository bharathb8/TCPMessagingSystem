package src;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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
				while(! Thread.currentThread().isInterrupted()) {
					message = this.mQueue.poll(10000, TimeUnit.MILLISECONDS);
					if (message != null) {
						outToClient.writeBytes(message + "\n");
					}
				}
			} catch (Exception e) {
				System.out.println("Caught : " + e);
			}
		}
	}

	protected long selfID;
	protected Socket connectionSocket;
	protected BlockingQueue<String> messageQueue;
	protected ServerReceiver messageReceiver;
	protected DispatchMessage dispatcher;

	public ClientConnection(long id, Socket s, BlockingQueue<String> mQ) {

		this.selfID = id;
		this.connectionSocket = s;
		this.messageQueue = mQ;

		//Sender(this.connectionSocket);
		//Sender.start();
	}

	public void start() {
		//Start message receiver thread
		this.messageReceiver = new ServerReceiver(this.connectionSocket, this.selfID);
		this.messageReceiver.start();

		// Start message dispatcher thread
		this.dispatcher = new DispatchMessage(this.connectionSocket, this.messageQueue);
		this.dispatcher.start();
	}

	public void disconnectConnection() {
		System.out.println("Stopping receiver service");
		this.messageReceiver.interrupt();
	}

}
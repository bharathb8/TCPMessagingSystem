/**
* Client Connection class
* This class maintains the connection for an active client.
* It listens to the incoming messages from the client, parses it out for commands
* Maintains a blocking queue of messages that are being sent to this client and
* delivers it to the client.
**/
package com;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ClientConnection {


	// This a thread that monitors the message queue and delivers the message to the client
	// when it receives a message from server or from other clients.
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
	// MessageQueue is essentially the inbox of the client. The server places the messages
	// sent by other clients in this queue and this thread delivers it to thread.
	protected BlockingQueue<String> messageQueue;
	// CommandReceiver is the command parser that listens to incoming commands from the client.
	protected CommandReceiver commandReceiver;
	// Message Deliverer
	protected DispatchMessage dispatcher;

	public ClientConnection(long id, Socket s, BlockingQueue<String> mQ) {
		this.selfID = id;
		this.connectionSocket = s;
		this.messageQueue = mQ;
	}

	public void start() {
		// Start command receiver thread
		this.commandReceiver = new CommandReceiver(this.connectionSocket, this.selfID);
		this.commandReceiver.start();

		// Start message dispatcher thread
		this.dispatcher = new DispatchMessage(this.connectionSocket, this.messageQueue);
		this.dispatcher.start();
	}

	/**
	* void disconnectConnection
	* this method will interrupt the command receiver thread.
	**/
	public void disconnectConnection() {
		this.commandReceiver.interrupt();
	}

}
package src;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ClientConnection {

	protected String selfID;
	protected Socket connectionSocket;
	protected BlockingQueue messageQueue;

	public ClientConnection(String id, Socket s) {

		this.selfID = id;
		this.connectionSocket = s;
		//this.messageQueue = mQueue;
		//Sender(this.connectionSocket);
		//Sender.start();
	}

	public void start() {
		Receiver r = new Receiver(this.connectionSocket);
		r.start();

	}

}
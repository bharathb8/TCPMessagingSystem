/**
* RelayService Class
* This class is a message delivery service. Server places the message into this service
* after parsing for recipients. The relay service ensures it places the messages to all the 
* required recipients' message queue. 
**/
package src;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RelayService extends Thread {

	// The server places messages into this queue.
	private BlockingQueue<Message> messagePool;
	// HashMap of client and their respective message queue. This is provided by the server. 
	private HashMap<Long, BlockingQueue<String>> msgQueueMap;

	public RelayService(HashMap<Long, BlockingQueue<String>> msgQMap) {
		this.msgQueueMap = msgQMap;
		this.messagePool = new LinkedBlockingQueue<Message>(100);
	}

	/**
	* boolean placeRequest(Message)
	* Place a new message in the delivery queue.
	**/
	public boolean placeRequest(Message msgObj) {
		try {
			this.messagePool.put(msgObj);
			return true;
		} catch (InterruptedException ie) {
			System.out.println("Caught InterruptedException while placing message pool request");
		}
		return false;
	}

	public void run() {

		Message msgRequest;
		try {
			// Message Pool is a blocking queue, that timesout every 10 seconds. If the thread hasn't been
			// interrupted, the service continues to poll for new messages.
			while(!Thread.currentThread().isInterrupted()) {
				msgRequest = this.messagePool.poll(10000, TimeUnit.MILLISECONDS);
				if (msgRequest!= null) {
					List<Long> recipients = msgRequest.recipients;
					StringBuilder sb = new StringBuilder();
					sb.append("Client " + msgRequest.sender + " says > ");
					sb.append(msgRequest.messageBody);
					String msgBody = sb.toString();
					// place the message in each of the recipient's message queue
					for (Long client: recipients) {
						if (this.msgQueueMap.containsKey(client)) {
							this.msgQueueMap.get(client).put(msgBody);
						}
					}
				}
			}
		} catch (InterruptedException ie) {
			System.out.println("Caught InterruptedException");
		}

	}
}
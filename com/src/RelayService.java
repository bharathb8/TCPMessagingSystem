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
	private BlockingQueue<Message> messagePool;
	private HashMap<Long, BlockingQueue<String>> msgQueueMap;

	public RelayService(HashMap<Long, BlockingQueue<String>> msgQMap) {
		this.msgQueueMap = msgQMap;
		this.messagePool = new LinkedBlockingQueue<Message>(100);
	}

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
			while(!Thread.currentThread().isInterrupted()) {
				msgRequest = this.messagePool.poll(10000, TimeUnit.MILLISECONDS);
				if (msgRequest!= null) {
					List<Long> recipients = msgRequest.recipients;
					StringBuilder sb = new StringBuilder();
					sb.append("From: " + msgRequest.sender);
					sb.append(", Message: " + msgRequest.messageBody);
					String msgBody = sb.toString();
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
package com;


import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandReceiverTest {
	
	private CommandReceiver commReceiver;
	private long mockClientID = 25;

	@Before
	public void setUp() throws Exception {
		Socket socket = mock(Socket.class);
		commReceiver = new CommandReceiver(socket, mockClientID);
	}

	@Test
	public void testWhoAmICommand() {
		long whoAmIResponse = commReceiver.getClientID();
		assertEquals(whoAmIResponse,mockClientID);

		commReceiver = new CommandReceiver(mock(Socket.class), (long)(22));
		assertEquals(commReceiver.getClientID(),22);
	}

	@Test
	public void testGetCSVString() {
		List<Long> userIDList = new ArrayList<Long>();
		userIDList.add(Long.valueOf(22));
		userIDList.add(Long.valueOf(20));
		userIDList.add(Long.valueOf(21));

		String csvString = commReceiver.getCSVString(userIDList);
		assertEquals(csvString, "22,20,21");
	}

	@Test
	public void testParseMessage() {
		// well-formed relay message of the format:
		// relay <list of clients> body: <message body>
		Message msg = commReceiver.parseMessage("relay 2,3,4 body: Hi There!");
		List<Long> recipientList = new ArrayList<Long>();
		recipientList.add(Long.valueOf(2));
		recipientList.add(Long.valueOf(3));
		recipientList.add(Long.valueOf(4));

		assertEquals(msg.getSender(), 25);
		assertEquals(msg.getRecipientsList(), recipientList);
		assertEquals(msg.getMessageBody(), " Hi There!");


		// Only valid IDs are extracted from the recipients CSV list.
		msg = commReceiver.parseMessage("relay 2,asas,4 body: Hi There!");
		recipientList = new ArrayList<Long>();
		recipientList.add(Long.valueOf(2));
		recipientList.add(Long.valueOf(4));
		assertEquals(msg.getSender(), 25);
		assertEquals(msg.getRecipientsList(), recipientList);
		assertEquals(msg.getMessageBody(), " Hi There!");

		msg = commReceiver.parseMessage("send-to: 2,3,4 body: Hi There!");
		// This message object will be null because the message string does not start with "relay"
		assertNull(msg);

		// broadcast puts all active users as recipient list.
		msg = commReceiver.parseMessage("broadcast Message: This is a broadcast message.");
		assertEquals(msg.getSender(), 25);
		assertEquals(msg.getMessageBody(), " This is a broadcast message.");
	}

	public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("com.CommandReceiverTest");
    }

}
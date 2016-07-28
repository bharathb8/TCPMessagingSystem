package com;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RelayServiceTest {
	
	private RelayService relayService;
	private BlockingQueue<Message> mPool;
	private HashMap<Long, BlockingQueue<String>> msgQueueMap;

	@Before
	public void setUp() throws Exception {
		msgQueueMap = new HashMap<Long, BlockingQueue<String>>();
		relayService = new RelayService(msgQueueMap);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testPlaceRelayRequest() {

		List<Long> recipientsList = new ArrayList<Long>();
		recipientsList.add((long) 2);
		relayService.placeRequest(new Message(1, recipientsList, "Hello"));
		int remainingCapacity = relayService.getRemainingCapacity();
		assertEquals(remainingCapacity,99);
	}

	public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("com.RelayServiceTest");
    }

}
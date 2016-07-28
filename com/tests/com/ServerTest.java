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

public class ServerTest {
		
	private Socket socket;

	@Before
	public void setUp() throws Exception {
		Socket socket = mock(Socket.class);
		Server.serverInit();
	}

	@Test
	public void testRegisterNewUser() {
		List<Long> activeUsers = Server.getActiveUsers();
		assertEquals(activeUsers.size(),0);

		Server.registerNewUser(Server.getNewUserID(), socket);
		activeUsers = Server.getActiveUsers();
		assertEquals(activeUsers.size(),1);

		Server.registerNewUser(Server.getNewUserID(), socket);
		activeUsers = Server.getActiveUsers();
		assertEquals(activeUsers.size(),2);

	}

	@Test
	public void testDisconnectUser() {
		List<Long> activeUsers = Server.getActiveUsers();
		assertEquals(activeUsers.size(),0);

		long user1 = Server.getNewUserID();
		Server.registerNewUser(user1, socket);
		activeUsers = Server.getActiveUsers();
		assertEquals(activeUsers.size(),1);

		Server.disconnectUser(user1);
		assertEquals(activeUsers.size(),0);

		user1 = Server.getNewUserID();
		Server.registerNewUser(user1, socket);
		
		user1 = Server.getNewUserID();
		Server.registerNewUser(user1, socket);
		
		Server.disconnectUser(user1);
		assertEquals(activeUsers.size(),1);

	}

	public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("com.ServerTest");
    }

}
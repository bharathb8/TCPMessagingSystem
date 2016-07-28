/**
* Message class
**/
package com;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Message {
	public long sender;
	public List<Long> recipients;
	public String messageBody;

	public Message(long sender, List<Long> recipients, String msg) {
		this.sender = sender;
		this.recipients = recipients;
		this.messageBody = msg;
	}

	public long getSender() {
		return this.sender;
	}

	public List<Long> getRecipientsList() {
		return this.recipients;
	}

	public String getMessageBody() {
		return this.messageBody;
	}

}
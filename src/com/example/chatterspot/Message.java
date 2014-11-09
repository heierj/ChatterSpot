package com.example.chatterspot;

import java.sql.Date;

public class Message {
	private String username;
	private String message;
	private Date timestamp;
	private int chatNumber;
	
	public Message(String username, String message, int chatNumber) {
		this.username = username;
		this.message = message;
		this.timestamp = new Date(System.currentTimeMillis());
		this.chatNumber = chatNumber;
	}
	
	public Message(String username, String message, Date time, int chatNumber) {
		this.username = username;
		this.message = message;
		this.timestamp = time;
		this.chatNumber = chatNumber;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public int getChatNumber() {
		return chatNumber;
	}
}

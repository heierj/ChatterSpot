package Shared;

import java.sql.Timestamp;

/**
 * Represents a chat room message. Contains all needed 
 * information for the message.
 */
public class Message {
	private String username;
	private String message;
	private Timestamp timestamp;
	private int chatroomID;
	
	/**
	 * This constructor will set the time stamp of the message to but the 
	 * current time
	 * 
	 * @param username of the message creator
	 * @param message content of message
	 * @param chatroomID the chat room which the message belongs in
	 */
	public Message(String username, String message, int chatroomID) {
		this.username = username;
		this.message = message;
		//this.timestamp = new Date(System.currentTimeMillis());
		this.chatroomID = chatroomID;
	}
	
	public Message(String username, String message, Timestamp timestamp, int chatroomID) {
		this.username = username;
		this.message = message;
		this.timestamp = timestamp;
		this.chatroomID = chatroomID;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public int getChatNumber() {
		return chatroomID;
	}
}

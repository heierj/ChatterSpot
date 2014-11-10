package Shared;

import java.sql.Date;

/**
 * Represents a chat room message. Contains all needed 
 * information for the message.
 */
public class Message {
	private String username;
	private String message;
	private Date timestamp;
	private int chatNumber;
	
	/**
	 * This constructor will set the time stamp of the message to but the 
	 * current time
	 * @param username of the message creator
	 * @param message content of message
	 * @param chatNumber the chat room which the message belongs in
	 */
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

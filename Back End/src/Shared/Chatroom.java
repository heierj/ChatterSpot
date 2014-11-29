package Shared;

import java.sql.Timestamp;

public class Chatroom {
	private String name;
	private Timestamp timestamp;
	private int id;
	
	public Chatroom(String name, Timestamp timestamp, int id) {
		this.name = name;
		this.timestamp = timestamp;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public int getId() {
		return id;
	}
}

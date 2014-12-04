package Shared;

import java.sql.Timestamp;

public class Chatroom {
	private String name;
	private Timestamp timestamp;
	private int id;
	private double lon;
	private double lat;
	
	public Chatroom(String name, Timestamp timestamp, int id, double lat, double lon) {
		this.name = name;
		this.timestamp = timestamp;
		this.id = id;
		this.lon = lon;
		this.lat = lat;
	}
	
	public Chatroom(String name, double lat, double lon) {
		this.name = name;
		this.lon = lon;
		this.lat = lat;
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
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
}

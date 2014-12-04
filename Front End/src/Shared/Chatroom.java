package Shared;

import java.sql.Timestamp;

public class Chatroom {
	private String name;
	private Timestamp timestamp;
	private int id;
	private double lon;
	private double lat;
	
	/** Not part of data model, used on app side */
	private float curDistance;
	
	public Chatroom(String name, Timestamp timestamp, int id, double lat, double lon) {
		this.name = name;
		this.timestamp = timestamp;
		this.id = id;
		this.lon = lon;
		this.lat = lat;
		curDistance = 0;
	}
	
	public Chatroom(String name, double lat, double lon) {
		this.name = name;
		this.lon = lon;
		this.lat = lat;
		curDistance = 0;
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

	public void setCurDist(float curDist) {
		this.curDistance = curDist;
	}
	
	public float getCurDist() {
		return curDistance;
	}
}
package com.example.chatterspot;

import java.util.ArrayList;
import java.util.List;

import Client.ChatroomClient;
import Shared.Chatroom;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class FindChatActivity extends Activity {
	protected static final int CHATROOM_RADIUS = 50;
	public final static String CHAT = "com.example.chatterspot.CHAT";
	protected ArrayList<Chatroom> chats;
	protected ChatroomClient client;
	protected boolean locationSet;
	protected FindChatLocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		chats = new ArrayList<Chatroom>();

		// Creates chat room client to load available chats
		client = new ChatroomClient(this);

		// Prepare location services
		locationSet = false;
		locationManager = new FindChatLocationManager(
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				this);
	}

	/**
	 * Adds a list of chats to the currently displayed chats
	 */
	public void addChatrooms(List<Chatroom> chatrooms) {
		if(chatrooms == null) return;
		chats.addAll(chatrooms);
	}
	
	/**
	 * Pause location updates
	 */
	@Override
	public void onPause() {
		super.onPause();
		locationSet = false;
		locationManager.pauseUpdates();
	}
	
	/**
	 * Resume location updates
	 */
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    locationManager.resumeUpdates();
	    setNewLocation(locationManager.getLocation());
	}
	
	/**
	 * This method is called when a new location is set by the location manager
	 */
	public void setNewLocation(Location location) {
		if(location != null) {
			locationSet = true;
		}
	}
}

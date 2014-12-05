package com.example.chatterspot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

import Client.ChatroomClient;
import Shared.Chatroom;
import Utils.ChatroomComparator;
import Utils.ChatroomUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FindChatActivity extends Activity implements AdapterView.OnItemClickListener {
	private static final int CHATROOM_RADIUS = 50;
	public final static String CHAT = "com.example.chatterspot.CHAT";
	private ArrayList<Chatroom> chats;
	private FindChatLocationManager locationManager;
	private boolean locationSet;
	private ChatroomClient client;
	private ChatAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_chat);
		
		chats = new ArrayList<Chatroom>();
		
	    // Creates chat room client to load available chats
		client = new ChatroomClient(this);
		
		// Prepare location services
		locationSet = false;
		locationManager = new FindChatLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE),
				this);
		
		getActionBar().setTitle("Explore Chats");
		 
		// Create the adapter for viewing chats and attach it to the view
		adapter = new ChatAdapter(this, chats);
		ListView chatView = (ListView) findViewById(R.id.chats);
		chatView.setAdapter(adapter);
		chatView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_add_chat:
			Intent intent = new Intent(this, CreateChatActivity.class);
			startActivityForResult(intent, 0);
	        return true;
		} 
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Adds a list of chats to the currently displayed chats
	 */
	public void addChatrooms(List<Chatroom> chatrooms) {
		if(chatrooms == null) return;
		chats.addAll(chatrooms);
		setNewLocation(locationManager.getLocation());
	}
	
	/**
	 * Updates the location of the user in the UI
	 * @param location
	 */
	public void setNewLocation(Location location) {
		if(location == null) {
			adapter.notifyDataSetChanged();
			return;
		} else {
			locationSet = true;
		}
		ChatroomUtils.setDistanceInFeet(chats, location);
		Collections.sort(chats, new ChatroomComparator());
		adapter.notifyDataSetChanged();
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
	    if(!locationManager.gpsEnabled()) {
	    	showGpsDialog();
	    }
	    setNewLocation(locationManager.getLocation());
	}
	
	/**
	 * Shows a dialag to the user letting them know they need to have
	 * GPS services turned on to use the application
	 */
	private void showGpsDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
        dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), 
        		new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
		                startActivity(myIntent);
		                //get gps
		            }
        		});
        dialog.setNegativeButton(this.getString(R.string.cancel), 
        		new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {}
        		});
        dialog.show();
	}
	
	/**
	 * Sends a newly created chat to the server
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  if(resultCode == Activity.RESULT_OK) {
		  String chatName = data.getStringExtra(CreateChatActivity.CHAT_NAME);
		  if(chatName == null) return;
		  Location lastLocation = locationManager.getLocation();
		  
		  if(lastLocation == null) {
			  showGpsDialog();
			  return;
		  }
		  
		  Chatroom chat = new Chatroom(chatName, null, resultCode, lastLocation.getLatitude(), lastLocation.getLongitude());
		  System.out.println("Chat: " + chat.getName());
		  client.createChat(chat);
	  }
	}

	/**
	 * Handles the event when a user is trying to join a chat
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	    Chatroom clickedChat = chats.get(position);
	    if (!locationManager.gpsEnabled()) {
	    	showGpsDialog();
	    } else if (!locationSet){
	    	// TODO: create a new dialog here
	    	showGpsDialog();
	    } else if(clickedChat.getCurDist() < CHATROOM_RADIUS) {
		    Intent intent = new Intent(FindChatActivity.this, ChatActivity.class);
		    intent.putExtra(CHAT, new Gson().toJson(clickedChat));
		    startActivity(intent);
	    } else {
	    	// Don't let them enter
	    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	        dialog.setMessage(this.getResources().getString(R.string.not_in_chat_range));
	        dialog.setPositiveButton(this.getResources().getString(R.string.ok), 
	        		new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
			            }
	        		});
	        dialog.show();
	    }
	}
}

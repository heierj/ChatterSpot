package com.example.chatterspot;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import Client.ChatroomClient;
import Shared.Chatroom;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FindChatActivity extends Activity implements LocationListener {
	public final static String CHAT = "com.example.chatterspot.CHAT";
	private ArrayList<Chatroom> chats;
	private ChatroomClient client;
	private ChatAdapter adapter;
	private LocationManager locationManager;
	private Location lastLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_chat);
		
		chats = new ArrayList<Chatroom>();
		
	    // Creates chat room client to load available chats
		client = new ChatroomClient(this);
		
		setupLocationManager();
		lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastLocation != null) {
			System.out.println("Found location");
		} else {
			lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(lastLocation == null) {
				System.out.println("Can't get last location");
			}
		}
		getActionBar().setTitle("Explore Chats");
		 
		adapter = new ChatAdapter(this, chats);
		adapter.setLocation(lastLocation);
		ListView chatView = (ListView) findViewById(R.id.chats);
		chatView.setAdapter(adapter);
		chatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> av, View view, int position, long arg3) {
			    Chatroom clickedChat = chats.get(position);
			    Intent intent = new Intent(FindChatActivity.this, ChatActivity.class);
			    intent.putExtra(CHAT, new Gson().toJson(clickedChat));
			    startActivity(intent);
			  }
		});
	}

	private void setupLocationManager() {
	    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
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
		for(Chatroom chatroom : chatrooms) {
			chats.add(chatroom);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    checkLocationService();
	}
	
	private void checkLocationService() {
		boolean gps_enabled = false;
		try{
	    	gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    } catch(Exception ex){}

	   if(!gps_enabled){
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
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  if(resultCode == Activity.RESULT_OK) {
		  String chatName = data.getStringExtra(CreateChatActivity.CHAT_NAME);
		  if(chatName == null) return;
		  if(lastLocation == null) {
			  lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		  }
		  Chatroom chat = new Chatroom(chatName, lastLocation.getLatitude(), lastLocation.getLongitude());
		  System.out.println("Chat: " + chat.getName());
		  client.createChat(chat);
	  }
	}

	@Override
	public void onLocationChanged(Location location) {
		lastLocation = location;
		adapter.setLocation(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}
}

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
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

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
	 * Shows a dialog to the user letting them know they need to have GPS
	 * services turned on to use the application
	 */
	protected void showGpsDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(this.getResources().getString(
				R.string.gps_network_not_enabled));
		dialog.setPositiveButton(
				this.getResources().getString(R.string.open_location_settings),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						Intent myIntent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(myIntent);
					}
				});
		dialog.setNegativeButton(this.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
					}
				});
		dialog.show();
	}

	/**
	 * Adds a list of chats to the currently displayed chats
	 */
	public void updateChatrooms(List<Chatroom> chatrooms) {
		if (chatrooms == null)
			return;
		chats.clear();
		chats.addAll(chatrooms);
	}

	protected void createChat(String chatName) {
		Location lastLocation = locationManager.getLocation();

		if (lastLocation == null) {
			showLocationNotSetCreateDialog();
			return;
		}

		Chatroom chat = new Chatroom(chatName, null, 0,
				lastLocation.getLatitude(), lastLocation.getLongitude());
		client.createChat(chat);
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
		super.onResume(); // Always call the superclass method first
		locationManager.resumeUpdates();
		setNewLocation(locationManager.getLocation());
	}

	/**
	 * This method is called when a new location is set by the location manager
	 */
	public void setNewLocation(Location location) {
		if (location != null) {
			locationSet = true;
		}
	}

	protected void showLocationNotSetCreateDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(this.getResources().getString(
				R.string.location_not_set_create));
		dialog.setPositiveButton(this.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
					}
				});
		dialog.show();
	}
	
	protected void showLocationNotSetDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(this.getResources().getString(
				R.string.location_not_set));
		dialog.setPositiveButton(this.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
					}
				});
		dialog.show();
	}

	protected void showNotInRangeDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(this.getResources().getString(
				R.string.not_in_chat_range));
		dialog.setPositiveButton(this.getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
					}
				});
		dialog.show();
	}

	protected boolean enterChat(Chatroom chat) {
		if (!locationManager.gpsEnabled()) {
			showGpsDialog();
		} else if (!locationSet) {
			showLocationNotSetDialog();
		} else if (chat.getCurDist() <= CHATROOM_RADIUS) {
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra(CHAT, new Gson().toJson(chat));
			startActivity(intent);
			return true;
		} else {
			showNotInRangeDialog();
		}
		return false;
	}
}

package com.example.chatterspot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import Shared.Chatroom;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapPane extends Activity {
	private GoogleMap mMap;
	private Marker currentPosition;
	private Circle radius;
	private List<Marker> chatrooms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		currentPosition = null;
		radius = null;
		chatrooms = new ArrayList<Marker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		placeCurrentPositionMarker(9, 9);
	}

	// create or move current position marker
	protected void placeCurrentPositionMarker(double lat, double lon) {
		if (currentPosition == null) {
			currentPosition = mMap.addMarker(new MarkerOptions()
					.position(new LatLng(lat, lon))
					.title("Me")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			// Create a new radius which shows roughly where the available
			// chatrooms are
			radius = mMap.addCircle(new CircleOptions().center(
					new LatLng(lat, lon)).radius(100)); // meters
		} else {
			currentPosition.setPosition(new LatLng(lat, lon));
			radius.setCenter(new LatLng(lat, lon));
		}
	}

	// set chatrooms on map to this list
	protected void setChatrooms(ArrayList<Chatroom> chatrooms) {
		for (int i = 0; i < chatrooms.size(); i++) {
			this.chatrooms.add(createChatroom(chatrooms.get(i)));
		}
	}

	// create a single chatroom
	private Marker createChatroom(Chatroom room) {
		BitmapDescriptor m = null;
		if (1 == new Random().nextInt()) { // needs to change available
			m = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
		} else {// unavailabe
			m = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED);
		}
		return mMap.addMarker(new MarkerOptions()
				.position(new LatLng(room.getLat(), room.getLon()))
				.title(room.getName()).icon(m));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_pane, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_list_view:
			Intent intent = new Intent(this, FindChatActivity.class);
			startActivity(intent);
	        return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	// set all chatrooms to be available or unavailable based on positions
	protected void updateChatrooms() {
		for (int i = 0; i < chatrooms.size(); i++) {
			BitmapDescriptor m = null;
			if (1 == new Random().nextInt()) { // needs to change available
				m = BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			} else {// unavailabe
				m = BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED);
			}
			chatrooms.get(i).setIcon(m);
		}
	}
}
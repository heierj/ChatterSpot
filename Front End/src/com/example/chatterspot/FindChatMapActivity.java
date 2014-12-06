package com.example.chatterspot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import Shared.Chatroom;
import Utils.ChatroomUtils;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class FindChatMapActivity extends FindChatActivity {
	private GoogleMap mMap;
	private Marker currentPosition;
	private Circle radius;
	private List<SpotMarker> chatrooms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		currentPosition = null;
		radius = null;
		chatrooms = new ArrayList<SpotMarker>();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			    @Override
			    public void onInfoWindowClick(Marker marker) {
			    		if(marker.equals(currentPosition)) {
			    			return;
			    		}
			    		
			    		for(int i = 0; i < chatrooms.size(); i ++) {
			    			if(marker.equals(chatrooms.get(i).marker)) {
			    				enterChat(chatrooms.get(i).chatroom);
			    			}
			    		}
			    }
		});
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
					new LatLng(lat, lon)).radius(CHATROOM_RADIUS / ChatroomUtils.METERS_TO_FEET)); // meters
			CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
			CameraUpdateFactory.zoomTo(4);
		} else {
			currentPosition.setPosition(new LatLng(lat, lon));
			radius.setCenter(new LatLng(lat, lon));
		}
	}

	// set chatrooms on map to this list
	protected void setChatrooms() {
		for (int i = 0; i < chats.size(); i++) {
			this.chatrooms.add(new SpotMarker(chats.get(i), drawChatroom(chats.get(i))));
		}
	}

	// create a single chatroom
	private Marker drawChatroom(Chatroom room) {
		BitmapDescriptor m = null;
		String snippet = "";
		if (1 == new Random().nextInt()) { // needs to change available
			m = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			snippet = "Click here to join";
		} else {// Unavailable
			m = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED);
			snippet = "Move " + (Math.abs(room.getCurDist() - CHATROOM_RADIUS)) + " closer to join"; 
		}
		return mMap.addMarker(new MarkerOptions()
				.position(new LatLng(room.getLat(), room.getLon()))
				.title(room.getName()).icon(m)
				.snippet(snippet));
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
			Intent intent = new Intent(this, FindChatListActivity.class);
			startActivity(intent);
	        return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	// set all chatrooms to be available or unavailable based on positions
	protected void updateChatrooms() {
		for (int i = 0; i < chatrooms.size(); i++) {
			BitmapDescriptor m = null;
			if (chats.get(i).getCurDist() <= CHATROOM_RADIUS) { // needs to change available
				m = BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			} else {// unavailabe
				m = BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED);
			}
			chatrooms.get(i).marker.setIcon(m);
		}
	}

	@Override
	public void setNewLocation(Location location) {
		super.setNewLocation(location);
		if(location == null) {
			return;
		}
		double lat, lon;
		lat = location.getLatitude();
		lon = location.getLongitude();
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
		updateChatrooms();
		ChatroomUtils.setDistanceInFeet(chats, location);
	}

	@Override
	public void updateChatrooms(List<Chatroom> chatrooms) {
		super.updateChatrooms(chatrooms);
		setChatrooms();
		
	}
}
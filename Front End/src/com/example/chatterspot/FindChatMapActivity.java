package com.example.chatterspot;

import java.util.ArrayList;
import java.util.List;

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
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

	// set chatrooms on map to this list
	private void setChatrooms() {
		for(SpotMarker spotMarker : chatrooms) {
			spotMarker.marker.remove();
		}
		this.chatrooms.clear();
		for (int i = 0; i < chats.size(); i++) {
			this.chatrooms.add(new SpotMarker(chats.get(i), drawChatroom(chats.get(i))));
		}
	}

	// create a single chatroom
	private Marker drawChatroom(Chatroom room) {
		BitmapDescriptor m = null;
		String snippet = "";
		if (locationSet && room.getCurDist() <= CHATROOM_RADIUS) { // needs to change available
			m = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			snippet = "Click here to join";
		} else {// Unavailable
			m = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED);
			if(!locationManager.providerEnabled()) {
				snippet = "Please Enable GPS to join chatrooms";
			}else if(locationSet) {
				snippet = "Move " + (Math.abs(room.getCurDist() - CHATROOM_RADIUS)) + " closer to join"; 
			}else {
				snippet = "Finding your location ...";
			}
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
		Intent intent;
		switch(item.getItemId()) {
		case R.id.action_list_view:
			intent = new Intent(this, FindChatListActivity.class);
			startActivity(intent);
	        return true;
	        
		case R.id.action_refresh_chat:
			client.loadChats();
			return true;
	        
		case R.id.action_add_chat:
			intent = new Intent(this, CreateChatActivity.class);
			intent.putExtra(LAT, locationManager.getLocation().getLatitude());
			intent.putExtra(LONG, locationManager.getLocation().getLongitude());
			startActivityForResult(intent, 0);
	        return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	// set all chatrooms to be available or unavailable based on positions
	protected void updateChatrooms() {
		for (int i = 0; i < chatrooms.size(); i++) {
			BitmapDescriptor m = null;
			String snippet = "";
			if (locationSet && chats.get(i).getCurDist() <= CHATROOM_RADIUS) { // needs to change available
				m = BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
				snippet = "Click here to join";
			} else {// Unavailable
				m = BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED);
				if(!locationManager.providerEnabled()) {
					snippet = "Please Enable GPS to join chatrooms";
				}else if(locationSet) {
					snippet = "Move " + (Math.round(Math.abs(chats.get(i).getCurDist() - CHATROOM_RADIUS))) + " feet closer to join"; 
				}else {
					snippet = "Finding your location ...";
				}
			}
			chatrooms.get(i).marker.setIcon(m);
			chatrooms.get(i).marker.setSnippet(snippet);
		}
	}

	/**
	 * Sends a newly created chat to the server
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  if(resultCode == Activity.RESULT_OK) {
		  String chatName = data.getStringExtra(CreateChatActivity.CHAT_NAME);
		  double lat = data.getDoubleExtra(CreateChatActivity.CHAT_LATITUDE, Integer.MIN_VALUE);
		  double lon = data.getDoubleExtra(CreateChatActivity.CHAT_LONGITUDE, Integer.MIN_VALUE);
		  if(lat == Integer.MIN_VALUE || lon == Integer.MIN_VALUE) {
			  createChat(chatName, null);
			  return;
		  }
		  if(chatName == null) return;
		  createChat(chatName, new LatLng(lat, lon)); 
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
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));
			currentPosition = mMap.addMarker(new MarkerOptions()
					.position(new LatLng(lat, lon))
					.title("Me")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			// Create a new radius which shows roughly where the available
			// chatrooms are
			radius = mMap.addCircle(new CircleOptions().strokeColor(Color.rgb(51, 102, 200)).fillColor(0x55336699).strokeWidth(3).center(
					new LatLng(lat, lon)).radius(CHATROOM_RADIUS / ChatroomUtils.METERS_TO_FEET)); // meters
		} else {
			currentPosition.setPosition(new LatLng(lat, lon));
			radius.setCenter(new LatLng(lat, lon));
		}
		updateChatrooms();
	}

	@Override
	public void updateChatrooms(List<Chatroom> chatrooms) {
		super.updateChatrooms(chatrooms);
		setChatrooms();
		
	}
}
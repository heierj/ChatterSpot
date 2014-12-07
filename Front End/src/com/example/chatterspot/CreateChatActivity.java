package com.example.chatterspot;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class CreateChatActivity extends Activity {
	public final static String CHAT_NAME = "com.example.chatterspot.CHAT_NAME";
	public final static String CHAT_LATITUDE = "com.example.chatterspot.CHAT_LATITUDE";
	public final static String CHAT_LONGITUDE = "com.example.chatterspot.CHAT_LONGITUDE";
	private GoogleMap mMap;
	private LatLng latlng = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chat);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		double lat = intent.getDoubleExtra(FindChatActivity.LAT, Integer.MIN_VALUE);
		double lon = intent.getDoubleExtra(FindChatActivity.LONG, Integer.MIN_VALUE);
		latlng = new LatLng(lat, lon);
		//map fragment
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
		    @Override
		    public void onMarkerDragEnd(Marker marker) {
		    		latlng = marker.getPosition();
		    }

			@Override
			public void onMarkerDrag(Marker marker) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMarkerDragStart(Marker marker) {
				// TODO Auto-generated method stub
				
			}
	});
		
	//create marker itself
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));
		mMap.addMarker(new MarkerOptions()
				.position(new LatLng(lat, lon))
				.title("New Chatroom Location")
				.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setDraggable(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_chat, menu);
		return true;
	}

	/**
	 * Handles buttons clicked in the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	public void createChat(View view) {
		EditText editText = (EditText) findViewById(R.id.enter_chat_name);
		Intent intent = new Intent();
		intent.putExtra(CHAT_NAME, editText.getText().toString());
		intent.putExtra(CHAT_LATITUDE, latlng.latitude);
		intent.putExtra(CHAT_LONGITUDE, latlng.longitude);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}

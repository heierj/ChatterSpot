package com.example.chatterspot;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * This class is to help manage location updates for the 
 * FindChatActivity
 */
public class FindChatLocationManager implements LocationListener {
	private LocationManager locationManager;
	private Location lastLocation;
	private boolean providerEnabled;
	private FindChatActivity activity;

	public FindChatLocationManager(LocationManager locationManager, FindChatActivity activity) {
		this.locationManager = locationManager;
		this.activity = activity;
	    resumeUpdates();
	}
	
	/**
	 * Sets the new location and updates it in the UI
	 */
	@Override
	public void onLocationChanged(Location location) {
		lastLocation = location;
		activity.setNewLocation(location);
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		providerEnabled = true;
	}

	@Override
	public void onProviderDisabled(String provider) {
		providerEnabled = false;
	}
	
	/**
	 * Used to pause updates when the user is not actively on the screen
	 */
	public void pauseUpdates() {
		locationManager.removeUpdates(this);
	}
	
	public void resumeUpdates() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	/**
	 * @return the last known location. If gps doesn't have location yet then
	 * return the coarse location
	 */
	public Location getLocation() {
		if(!providerEnabled) {
			return null;
		}
		if(lastLocation == null) {
			lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return lastLocation;
	}

	public boolean gpsEnabled() {
	    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}

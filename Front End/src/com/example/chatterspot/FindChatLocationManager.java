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
	private static int TWO_MINUTES = 120000;
	private LocationManager locationManager;
	private Location lastLocation;
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
		if(isBetterLocation(location)) {
			lastLocation = location;
			activity.setNewLocation(location);
		}
	}
	
	private boolean isBetterLocation(Location location) {
		if(lastLocation == null) {
			return true;
		}
		
		// Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - lastLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - lastLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate) {
	        return true;
	    }
	    return false;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}
	
	/**
	 * Used to pause updates when the user is not actively on the screen
	 */
	public void pauseUpdates() {
		locationManager.removeUpdates(this);
	}
	
	public void resumeUpdates() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	
	/**
	 * @return the last known location. If gps doesn't have location yet then
	 * return the coarse location
	 */
	public Location getLocation() {
		if(lastLocation == null) {
			lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return lastLocation;
	}

	public boolean providerEnabled() {
	    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
	    		locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
}

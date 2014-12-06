package Utils;

import java.util.List;

import Shared.Chatroom;
import android.location.Location;

public class ChatroomUtils {
	public static float METERS_TO_FEET = 3.28084f;

	public static void setDistanceInFeet(List<Chatroom> chats, Location loc) {
		for(Chatroom chat : chats) {
			if(chat.getLat() != 0 && chat.getLon() != 0 && loc != null) {
				float[] result = new float[1];
				Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), 
						chat.getLat(), chat.getLon(), result);
				float dist = result[0];
				
				// Convert to feet
				dist *= METERS_TO_FEET;
				chat.setCurDist(dist);
			} else {
				chat.setCurDist(0);
			}
		}
	}
}

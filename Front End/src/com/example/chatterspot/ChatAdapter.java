package com.example.chatterspot;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import Shared.Chatroom;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<Chatroom> {
	private Location loc;

	public ChatAdapter(Context context, ArrayList<Chatroom> chats) {
		super(context, 0, chats);
	}

	public void setLocation(Location location) {
		this.loc = location;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Chatroom chat = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.chat_row, parent, false);
		}
		
		TextView name = (TextView) convertView.findViewById(R.id.CHAT_NAME);
		TextView createTime = (TextView) convertView.findViewById(R.id.CHAT_CREATE_TIME);
		TextView distance = (TextView) convertView.findViewById(R.id.CHAT_DISTANCE);
		
		String timestamp = "";
		if(chat.getTimestamp() != null) {
			long time = chat.getTimestamp().getTime();
			Date date = new Date(time);
			SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.US);
			timestamp = "Created: " + df.format(date);
		} 
		
		String chatDistance = "";
		if(chat.getLat() != 0 && chat.getLon() != 0 && loc != null) {
			float[] result = new float[1];
			Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), 
					chat.getLat(), chat.getLon(), result);
			float dist = result[0];
			
			// Convert to feet
			dist *= 3.28084;
			chatDistance += Math.round(dist) + " ft";
		}
		
		name.setText(chat.getName());
		createTime.setText(timestamp);
		distance.setText(chatDistance);
		
		return convertView;
	}
}

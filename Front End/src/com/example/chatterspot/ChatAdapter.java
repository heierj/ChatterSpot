package com.example.chatterspot;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import Shared.Chatroom;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<Chatroom> {

	public ChatAdapter(Context context, ArrayList<Chatroom> chats) {
		super(context, 0, chats);
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
		
		String timestamp = "";
		if(chat.getTimestamp() != null) {
			long time = chat.getTimestamp().getTime();
			Date date = new Date(time);
			SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.US);
			timestamp = "Created: " + df.format(date);
		} 
		
		name.setText(chat.getName());
		createTime.setText(timestamp);
		
		return convertView;
	}
}
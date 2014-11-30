package com.example.chatterspot;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import Shared.Message;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<Message> {

	public MessageAdapter(Context context, ArrayList<Message> messages) {
		super(context, 0, messages);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message message = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.message_row, parent, false);
		}
		
		TextView user = (TextView) convertView.findViewById(R.id.USER);
		TextView text = (TextView) convertView.findViewById(R.id.MESSAGE);
		TextView postTime = (TextView) convertView.findViewById(R.id.TIME);
		
		String timestamp = "";
		if(message.getTimestamp() != null) {
			long time = message.getTimestamp().getTime();
			Date date = new Date(time);
			SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.US);
			timestamp = df.format(date);
		} 
		
		user.setText(message.getUsername() + ":  ");
		text.setText(message.getMessage());
		postTime.setText(timestamp);
		
		return convertView;
	}
}

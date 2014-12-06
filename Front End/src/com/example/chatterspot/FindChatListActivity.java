package com.example.chatterspot;

import java.util.Collections;
import java.util.List;

import Shared.Chatroom;
import Utils.ChatroomComparator;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FindChatListActivity extends FindChatActivity implements AdapterView.OnItemClickListener {
	private ChatAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_chat);
		
		getActionBar().setTitle("Explore Chats");
		 
		// Create the adapter for viewing chats and attach it to the view
		adapter = new ChatAdapter(this, chats);
		ListView chatView = (ListView) findViewById(R.id.chats);
		chatView.setAdapter(adapter);
		chatView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_add_chat:
			Intent intent = new Intent(this, CreateChatActivity.class);
			startActivityForResult(intent, 0);
	        return true;
	        
		case R.id.action_refresh_chat:
			client.loadChats();
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Adds a list of chats to the currently displayed chats
	 */
	@Override
	public void updateChatrooms(List<Chatroom> chatrooms) {
		super.updateChatrooms(chatrooms);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Updates the location of the user in the UI
	 * @param location
	 */
	@Override
	public void setNewLocation(Location location) {
		super.setNewLocation(location);
		if(location == null) {
			return;
		} 
		Collections.sort(chats, new ChatroomComparator());
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Sends a newly created chat to the server
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  if(resultCode == Activity.RESULT_OK) {
		  String chatName = data.getStringExtra(CreateChatActivity.CHAT_NAME);
		  if(chatName == null) return;
		  createChat(chatName);
		  
	  }
	}

	/**
	 * Handles the event when a user is trying to join a chat
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	    Chatroom clickedChat = chats.get(position);
	    enterChat(clickedChat);
	}
}
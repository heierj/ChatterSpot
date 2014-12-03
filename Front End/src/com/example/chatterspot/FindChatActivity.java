package com.example.chatterspot;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import Client.ChatroomClient;
import Shared.Chatroom;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FindChatActivity extends Activity {
	public final static String CHAT = "com.example.chatterspot.CHAT";
	private ArrayList<Chatroom> chats;
	private ChatroomClient client;
	private ChatAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_chat);
		
		chats = new ArrayList<Chatroom>();
		
	    // Creates chat room client to load available chats
		client = new ChatroomClient(this);
		
		getActionBar().setTitle("Explore Chats");
		 
		adapter = new ChatAdapter(this, chats);
		ListView chatView = (ListView) findViewById(R.id.chats);
		chatView.setAdapter(adapter);
		chatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> av, View view, int position, long arg3) {
			    Chatroom clickedChat = chats.get(position);
			    Intent intent = new Intent(FindChatActivity.this, ChatActivity.class);
			    intent.putExtra(CHAT, new Gson().toJson(clickedChat));
			    startActivity(intent);
			  }
		});
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
		} 
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Adds a list of chats to the currently displayed chats
	 */
	public void addChatrooms(List<Chatroom> chatrooms) {
		if(chatrooms == null) return;
		for(Chatroom chatroom : chatrooms) {
			chats.add(chatroom);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  if(resultCode == Activity.RESULT_OK) {
		  String chatName = data.getStringExtra(CreateChatActivity.CHAT_NAME);
		  if(chatName == null) return;
		  Chatroom chat = new Chatroom(chatName);
		  System.out.println("Chat: " + chat.getName());
		  client.createChat(chat);
	  }
	}
}

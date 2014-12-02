package com.example.chatterspot;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import Client.ChatClient;
import Shared.Chatroom;
import Shared.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This activity creates the view for a chat room.
 */
public class ChatActivity extends Activity {
	private ArrayList<Message> messages;
	private MessageAdapter adapter;
	private User user;
	private ChatClient client;
	private Chatroom chatroom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		user = User.getInstance();
		chatroom = loadChatroom();
		
		// Set up the adapter to display messages
		messages = new ArrayList<Message>();
		adapter = new MessageAdapter(this, messages);
		ListView messageView = (ListView) findViewById(R.id.messages);
		messageView.setAdapter(adapter);
		
		// Create the client for network operations
		client = new ChatClient(this);
	}
	
	/**
	 * Loads the chatroom object sent from the findChatActivity
	 */
	private Chatroom loadChatroom() {
		Intent intent = getIntent();
		String chatJson = intent.getStringExtra(FindChatActivity.CHAT);
		return new Gson().fromJson(chatJson, Chatroom.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
	        return true;
		} 
		return super.onOptionsItemSelected(item);
	}
	
	/** 
	 * Called when the user clicks the Send button. Sends the new
	 * message to the database and displays it to the user.
	 */
	public void sendMessage(View view) {
		EditText editText = (EditText) findViewById(R.id.edit_message);
		Message newMessage = new Message(user.getUsername(), editText.getText().toString(), chatroom.getId());
	
		// Clear the message and put the keyboard away
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		editText.setText("");
		
		addMessage(newMessage);
		client.sendMessage(newMessage);
	}
	
	/**
	 * Adds a list of messages to the currently displayed messages
	 * @param newMessages the messages to be added
	 */
	public void addMessages(List<Message> newMessages) {
		for(Message message : newMessages) {
			addMessage(message);
		}
	}
	
	/**
	 * Adds a single message to the currently displayed messages
	 * @param message the message to be displayed
	 */
	public void addMessage(Message message) {
		messages.add(message);
		adapter.notifyDataSetChanged();
	}


	/**
	 * @return the chatId for the current chat
	 */
	public int getChatId() {
		return chatroom.getId();
	}
	
}

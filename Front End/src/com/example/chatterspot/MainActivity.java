package com.example.chatterspot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Client.ChatClient;
import Shared.Message;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE = "com.example.chatterspot.MESSAGE";
	private List<HashMap<String, String>> messages;
	private SimpleAdapter adapter;
	private static String USERNAME;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get the username from the intent
	    Intent intent = getIntent();
	    USERNAME = intent.getStringExtra(LoginActivity.USERNAME);
		
		messages = new ArrayList<HashMap<String, String>>();
		adapter = new SimpleAdapter(this, messages, R.layout.message_row,
				new String[] {"user", "message"}, new int[] {R.id.USER, R.id.MESSAGE});
		ListView messages = (ListView) findViewById(R.id.messages);
		messages.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		EditText editText = (EditText) findViewById(R.id.edit_message);
		
		Message newMessage = new Message(USERNAME, editText.getText().toString(), 0);
		new ChatClient.SendMessage().execute(newMessage);
		HashMap<String, String> message = new HashMap<String, String>();
		message.put("user", USERNAME + ": ");
		message.put("message", editText.getText().toString());
		messages.add(message);
		adapter.notifyDataSetChanged();
	}
	
}

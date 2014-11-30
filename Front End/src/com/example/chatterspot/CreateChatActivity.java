package com.example.chatterspot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class CreateChatActivity extends Activity {
	public final static String CHAT_NAME = "com.example.chatterspot.CHAT_NAME";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chat);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_chat, menu);
		return true;
	}

	/**
	 * Handles buttons clicked in the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void createChat(View view) {
		
		EditText editText = (EditText) findViewById(R.id.enter_chat_name);
		
		Intent intent = new Intent();
		intent.putExtra(CHAT_NAME, editText.getText().toString());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}

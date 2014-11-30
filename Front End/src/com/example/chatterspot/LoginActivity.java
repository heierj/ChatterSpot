package com.example.chatterspot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void startChatting(View view) {
		EditText editText = (EditText) findViewById(R.id.enter_username);
		
		Intent intent = new Intent(this, FindChatActivity.class);
		
		// Set up the singleton class of user
	    String username = editText.getText().toString();
	    User user = User.getInstance();
	    user.setUsername(username);
	    startActivity(intent);
	    finish();
	}
}

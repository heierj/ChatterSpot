package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import Shared.Message;
import android.os.AsyncTask;
import com.example.chatterspot.ChatActivity;
import com.google.gson.Gson;


/**
 * Creates a connection with the server which is used to 
 * send messages to a chat room
 *
 */
public class ChatClient {
	private static final String SERVER_URL = "http://173.250.159.247:4444";
	private ChatActivity chat; 
	
	public ChatClient(ChatActivity chat) {
		this.chat = chat;
	}
	
	/**
	 * Sends a message to the server
	 * @param message the message to be sent
	 */
	public void sendMessage(Message message) {
		new SendMessage().execute(message);
	}

	public void loadMessages() {
		new LoadMessages().execute();
	}
	
	public void updateMessage() {
		new UpdateMessages().execute();
	}
	
	/**
	 * This class is used to send a message to the server. It is needed
	 * since network operations must be done on a separate thread than
	 * the UI thread
	 */
	private class SendMessage extends AsyncTask<Message, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Message... messages) {
			Message message = messages[0];
			
			// Create the URL to send request to
			URL postMessage;
			try {
				postMessage = new URL(SERVER_URL + "/chatroom?id=" + chat.getChatId());
			} catch (MalformedURLException e1) {
				System.err.println("Post message URL is invalid");
				return false;
			}
			
			// Set up the HTTP 
			HttpURLConnection client;
			try {
				client = (HttpURLConnection) postMessage.openConnection();
				client.setDoOutput(true);
			} catch (IOException e) {
				System.err.println("Could not open HTTP connection to post message");
				return false;
			}
			
			try {
				client.setRequestMethod("POST");
			} catch (ProtocolException e) {
				System.err.println("Invalid request method set");
				client.disconnect();
				return false;
			}
			
			// Format the message
			Gson gson = new Gson();
			String json = gson.toJson(message);
			
			// Send the message
			DataOutputStream outToServer;		
			try {
				outToServer = new DataOutputStream(client.getOutputStream());
				outToServer.write(json.getBytes());
				outToServer.flush();
				outToServer.close();
			} catch (IOException e) {
				System.err.println("Cound't send message to server" + e);
				client.disconnect();
				return false;
			}
			
			try {
				System.out.println("Response: " + client.getResponseCode());
			} catch (IOException e) {
				System.err.println("No response received");
				client.disconnect();
				return false;
			}
			
			// Success!
			client.disconnect();
			return true;
		}
	}
	
	
	/**
	 * Loads all the messages for the chat room
	 * @return a list containing all the messages in the chat room
	 */
	public class LoadMessages extends AsyncTask<Void, Void, List<Message>> {

		@Override
		protected List<Message> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private class UpdateMessages extends AsyncTask<Void, Void, List<Message>> {

		@Override
		protected List<Message> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}

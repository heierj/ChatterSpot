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

import com.google.gson.Gson;


/**
 * Creates a connection with the server which is used to 
 * send messages to a chat room
 *
 */
public class ChatClient {
	private static final String SERVER_URL = "http://140.142.244.90:4444";

	
	public static class SendMessage extends AsyncTask<Message, Void, Boolean> {
		/**
		 * Sends a message to the server
		 * @param message the message to be sent
		 * @return true on success, false on failure
		 */
		@Override
		protected Boolean doInBackground(Message... messages) {
			System.out.println("Send message");
			Message message = messages[0];
			
			// Create the URL to send request to
			URL postMessage;
			try {
				postMessage = new URL(SERVER_URL);
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
				return false;
			}
			
			try {
				client.getResponseCode();
			} catch (IOException e) {
				System.err.println("No response received");
				return false;
			}
			// Success!
			return true;
		}
	}
	
	
	/**
	 * Loads all the messages for the chat room
	 * @return a list containing all the messages in the chat room
	 */
	public static List<Message> loadMessages() {
		return null;
	}
	
	/**
	 * Loads all the messages added to the chat room since the last
	 * check
	 * @return only the new messages
	 */
	public static List<Message> updateMessages() {
		return null;
	}
	
}

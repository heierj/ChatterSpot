package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

import Shared.Message;
import android.os.AsyncTask;

import com.example.chatterspot.ChatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Creates a connection with the server which is used to send messages to a chat
 * room
 * 
 */
public class ChatClient extends AbstractClient {
	private ChatActivity chat;

	public ChatClient(ChatActivity chat) {
		this.chat = chat;
		new LoadMessages().execute();
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

	/**
	 * This class is used to send a message to the server. It is needed since
	 * network operations must be done on a separate thread than the UI thread
	 */
	private class SendMessage extends AsyncTask<Message, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Message... messages) {
			Message message = messages[0];

			// Create the URL to send request to
			String url = SERVER_URL + "/chatroom";

			// Set up the HTTP
			HttpURLConnection client = openConnection(url, true, false);

			if (client == null) {
				System.err.println("Could not open HTTP "
						+ "connection to post message");
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
		private long lastUpdate = 0;
		private static final int LOAD_MESSAGE_TIMEOUT = 300000;

		/**
		 * Loads all of the messages when a chat is newly entered. 
		 */
		@Override
		protected List<Message> doInBackground(Void... params) {
			System.out.println("loading messages");
				String url;

				// Set URL based on whether we are loading all messages
				// or updating them
				if (lastUpdate == 0) {
					lastUpdate = System.currentTimeMillis();
					url = SERVER_URL + "/chatroom?id=" + chat.getChatId();
				} else {
					url = SERVER_URL + "/chatroom?id=" + chat.getChatId()
							+ "lastUpdate=" + lastUpdate;
				}

				// Open the HTTP connection
				HttpURLConnection client = openConnection(url, false, true);
				if (client == null) {
					System.err.println("Could not open " + ""
							+ "HTTP connection to load messages");
					return null;
				}

				try {
					client.setRequestMethod("GET");
					client.setReadTimeout(LOAD_MESSAGE_TIMEOUT);
				} catch (ProtocolException e) {
					System.err.println("Invalid request method set");
					client.disconnect();
					return null;
				}

				// Read the response from the server
				BufferedReader in;
				try {
					int responseCode = client.getResponseCode();
					if (responseCode == 200) {
						in = new BufferedReader(new InputStreamReader(
								client.getInputStream()));
					} else {
						System.err.println("Server returned response code"
								+ responseCode);
						client.disconnect();
						return null;
					}
				} catch (IOException e) {
					System.err.println("Could not read from server");
					client.disconnect();
					return null;
				}

				List<Message> updatedMessages = parseServerResponse(in);
				System.out.println("Messages length: " + updatedMessages.size());

				try {
					in.close();
					client.disconnect();
				} catch (IOException e) {
					System.err.println("Could not close connection");
				}

				return updatedMessages;
		}
		
		protected void onPostExecute(List<Message> messages) {
			chat.addMessages(messages);
		}

		/**
		 * When loading messages this method is used to parse the servers
		 * response
		 * @param response the response to be parsed
		 * @return the list of messages the server returned
		 */
		private List<Message> parseServerResponse(BufferedReader response) {
			String inputLine;
			StringBuffer buffer = new StringBuffer();

			// Read the messages JSON
			try {
				while ((inputLine = response.readLine()) != null) {
					buffer.append(inputLine);
				}
			} catch (IOException e) {
				System.err.println("Error parsing messages from server");
				return null;
			}

			// Parse the json into a list of messages
			String json = buffer.toString();
			System.out.println("Server response: " + json);
			Type listType = new TypeToken<ArrayList<Message>>() {
			}.getType();
			return new Gson().fromJson(json, listType);
		}
	}

}

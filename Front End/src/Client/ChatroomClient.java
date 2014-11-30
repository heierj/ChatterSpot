package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

import Shared.Chatroom;
import android.os.AsyncTask;

import com.example.chatterspot.FindChatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ChatroomClient extends AbstractClient {
	private FindChatActivity findChat;

	public ChatroomClient(FindChatActivity findChat) {
		this.findChat = findChat;
		new LoadChats().execute();
	}

	/**
	 * Loads all the chats user can enter
	 * @return a list containing all the chats available
	 */
	public class LoadChats extends AsyncTask<Void, Void, List<Chatroom>> {

		/**
		 * Loads all of the chats once user has signed in
		 */
		@Override
		protected List<Chatroom> doInBackground(Void... params) {
			System.out.println("loading chats");
			String url = SERVER_URL + "/chatrooms";

			// Open the HTTP connection
			HttpURLConnection client = openConnection(url, false, true);
			if (client == null) {
				System.err.println("Could not open " + ""
						+ "HTTP connection to load messages");
				return null;
			}

			try {
				client.setRequestMethod("GET");
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

			List<Chatroom> chatrooms = parseServerResponse(in);
			System.out.println("Chatrooms length: " + chatrooms.size());

			try {
				in.close();
				client.disconnect();
			} catch (IOException e) {
				System.err.println("Could not close connection");
			}

			return chatrooms;
		}

		protected void onPostExecute(List<Chatroom> chats) {
			findChat.addChatrooms(chats);
		}

		/**
		 * When loading chats this method is used to parse the servers
		 * response
		 * @param response the response to be parsed
		 * @return the list of chatrooms the server returned
		 */
		private List<Chatroom> parseServerResponse(BufferedReader response) {
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

			// Parse the json into a list of chats
			String json = buffer.toString();
			System.out.println("Server response: " + json);
			Type listType = new TypeToken<ArrayList<Chatroom>>() {
			}.getType();
			return new Gson().fromJson(json, listType);
		}
	}
	

}

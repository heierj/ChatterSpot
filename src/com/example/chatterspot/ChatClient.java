package com.example.chatterspot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import com.google.gson.Gson;


/**
 * Creates a connection with the server which is used to 
 * send messages to a chat room
 *
 */
public class ChatClient {
	private static final String SERVER_URL = "attu.washington.edu";
	private String username;


	
	/**
	 * Sets up a client session. Connects to the server and sets up
	 * the output and input streams for communicating with the server.
	 * @param username
	 */
	public ChatClient(String username) {
		this.username = username;
		
	}
	
	/**
	 * Sends a message to the server
	 * @param message the message to be sent
	 * @return true on success, false on failure
	 */
	public boolean sendMessage(String message) {
		// Create the URL to send request to
		URL postMessage;
		try {
			postMessage = new URL(SERVER_URL + "/message");
		} catch (MalformedURLException e1) {
			System.err.println("Post message URL is invalid");
			return false;
		}
		
		// Set up the HTTP 
		HttpURLConnection client;
		try {
			client = (HttpURLConnection) postMessage.openConnection();
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
		
		// Create the message
		Message outgoing = new Message(username, message, 0);
		Gson gson = new Gson();
		String json = gson.toJson(outgoing);
		
		// Send the message
		DataOutputStream outToServer;		
		try {
			outToServer = new DataOutputStream(client.getOutputStream());
			outToServer.writeBytes(json);
			outToServer.flush();
			outToServer.close();
		} catch (IOException e) {
			System.err.println("Cound't send message to server");
			return false;
		}
		
		// Success!
		return true;
	}
	
	/**
	 * Loads all the messages for the chat room
	 * @return a list containing all the messages in the chat room
	 */
	public List<Message> loadMessages() {
		return null;
	}
	
	/**
	 * Loads all the messages added to the chat room since the last
	 * check
	 * @return only the new messages
	 */
	public List<Message> updateMessages() {
		return null;
	}
	
}

package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Database.DatabaseInteraction;
import Shared.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/** 
 * Handles RESTful HTTP requests from the client.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
public class ClientHandler implements HttpHandler {

	/*
	 * Currently the following URL endpoints are supported:
	 * 
	 * GET:
	 * 	"/chatroom/<chat_id>" - Gets all the messages associated with a 
	 * 							particular chatroom id. A response will 
	 * 							be sent with the below JSON format.
	 * 
	 * 		{
  	 *		messages : [{ username : "<username>"
  	 *					  timestamp : "<time_stamp>"
  	 *					  message : "<message_text>" },
  	 *					...
  	 *				   ]
 	 * 		}
	 * 		
	 * 
	 * POST:
	 * 	"/chatroom" - Assumes the body of the message is JSON of the
	 * 			 	  below format
	 * 
	 * 		{
	 * 		username : "<username>"
	 * 		chatId : <number_id>
	 * 		message : "<message_text>"
	 * 		}
	 */
	
	/**
	 * {@inheritDoc}
	 */
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		DatabaseInteraction db = new DatabaseInteraction();
		try {
			db.open();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (requestMethod.equals("GET")) {
			handleGet(exchange, db);
		} else if (requestMethod.equals("POST")) {
			handlePost(exchange, db);
		} else {
			System.err.println("Unsupported request method used in HTTP message");
		}
		try {
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles a GET request to the server from the client
	 * @param db 
	 */
	private void handleGet(HttpExchange exchange, DatabaseInteraction db) {
		System.out.println("Get request received");
		String[] path = exchange.getRequestURI().getPath().split("/");
		
		if (path.length == 0) {
			System.err.println("Empty URL path");
			return;
		}
		
		int chatId;
		if (path[0].equalsIgnoreCase("chatroom")) {
			chatId = Integer.parseInt(path[1]);
		}
		
		ArrayList<Message> list;
		try {
			list = db.getMessages(0);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		Gson gson = new GsonBuilder()
	            .create();

	    String json = gson.toJson(list);
		try {
			exchange.sendResponseHeaders(200, json.getBytes().length);
			
			OutputStream os = exchange.getResponseBody();
			os.write(json.getBytes());
			os.close();
		} catch (IOException e) {
			System.err.println("Unable to send response to client: " + e.getMessage());
			return;
		}
		

	}
	
	/**
	 * Handles a POST request to the server from the client
	 * @param db 
	 */
	private void handlePost(HttpExchange exchange, DatabaseInteraction db) {
		String[] path = exchange.getRequestURI().getPath().split("/");
		
		if (path.length == 0) {
			System.err.println("Empty URL path");
			return;
		}
		
		BufferedReader reqBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
		String inputLine;
		StringBuffer buffer = new StringBuffer();

		// Read the messages JSON
		try {
			while ((inputLine = reqBody.readLine()) != null) {
				buffer.append(inputLine);
			}
		} catch (IOException e) {
			System.err.println("Error parsing message from client");
		}
		
		String json = buffer.toString();
		Message message = new Gson().fromJson(json, Message.class);
			try {
				db.addMessage(message);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Message sent to database");
			try {
				exchange.sendResponseHeaders(200, -1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

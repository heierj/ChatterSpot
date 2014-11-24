package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import Database.DatabaseInteraction;
import Database.databaseOperations;
import Shared.Message;

import com.google.gson.Gson;
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
  	 *		"messages" : [{ username : "<username>"
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
	 * 		"username" : "<username>"
	 * 		"chatId" : <number_id>
	 * 		"message" : "<message_text>"
	 * 		}
	 */
	
	/**
	 * {@inheritDoc}
	 */
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		
		if (requestMethod.equals("GET")) {
			handleGet(exchange);
		} else if (requestMethod.equals("POST")) {
			handlePost(exchange);
		} else {
			System.err.println("Unsupported request method used in HTTP message");
			return;
		}
	}
	
	/**
	 * Handles a GET request to the server from the client
	 */
	private void handleGet(HttpExchange exchange) {
		System.out.println("Get request received");
		String[] path = exchange.getRequestURI().getPath().split("/");
		
		if (path.length == 0) {
			System.err.println("Empty URL path");
			return;
		}
		
		int chatId = 1;
		if (path[0].equalsIgnoreCase("chatroom")) {
			chatId = Integer.parseInt(path[1]);
		}
		
		DatabaseInteraction dbi = new DatabaseInteraction();
		List<Message> messages;
		try {
  		dbi.open();
  		messages = dbi.getMessages(chatId);
  		dbi.close();
		} catch (Exception e) {
		  // Handle
		  System.err.println("Unable to open database connection: " + e.getMessage());
		  return;
		}
		
		StringBuffer responseBuffer = new StringBuffer();
		responseBuffer.append("{ \"messages\" : [");
		
		Iterator<Message> itr = messages.iterator();
		Gson gson = new Gson();
		
		// Iterator through all the messages, building up the JSON results
		while (itr.hasNext()) {
		  Message message = itr.next();
		  responseBuffer.append(gson.toJson(message, Message.class));
		  
		  if (itr.hasNext()) {
		    responseBuffer.append(",");
		  }
		}
		responseBuffer.append("] }");
		
		String jsonResponse = responseBuffer.toString();
		
		try {
			exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
			
			OutputStream os = exchange.getResponseBody();
			os.write(jsonResponse.getBytes(), 0, jsonResponse.getBytes().length);
			os.close();
		} catch (IOException e) {
			System.err.println("Unable to send response to client: " + e.getMessage());
			return;
		}
	}
	
	/**
	 * Handles a POST request to the server from the client
	 */
	private void handlePost(HttpExchange exchange) {
		String[] path = exchange.getRequestURI().getPath().split("/");
		
		if (path.length == 0) {
			System.err.println("Empty URL path");
			return;
		}
		
		// Parse requestBody JSON
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
		
		// TODO: Update DB to hold the message attached in the POST
		try {
			DatabaseInteraction dbi = new DatabaseInteraction();
			dbi.open();
			dbi.addMessage(message);
			dbi.close();
			
			System.out.println("Message sent to database");
			exchange.sendResponseHeaders(200, -1);

		} catch (Exception e) {
      System.err.println("Error inserting message into database: " + e.getMessage());
      return;
		}
	}
}

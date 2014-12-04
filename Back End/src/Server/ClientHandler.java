package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import Database.DatabaseInteraction;
import Shared.Chatroom;
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
	 * 	"/chatroom?id=<chatID>" - Gets all the messages associated with a 
	 * 						      particular chatroom id. A response will 
	 * 							  be sent with the JSON format below.
	 * 
	 * 		[  {"username" : "<username>",
  	 *			"timestamp" : "<time_stamp>",
  	 *			"message" : "<message_text>" },
  	 *			...
  	 *		]
  	 *	
  	 *	"/chatroom"  - Gets info for all the chatrooms 
  	 *
  	 *		[  {"name" : "<chatroom_name>",
  	 *		    "timestamp" : "<created_timestamp>",
  	 *		    "id" : "<chat_id>"},
  	 *          ...
  	 *      ]
	 *
	 * 		
	 * 
	 * POST:
	 * 	"/chatroom" - Posts a message to a particular chatroom. Assumes the body 
	 * 				  of the message is JSON of the below format.
	 * 
	 * 		{
	 * 		"name" : "<username>"
	 * 		"id" : <number_id>
	 * 		"message" : "<message_text>"
	 * 		}
	 * 
	 * 	"/chatroom/create" - Creates a chatroom with the specified parameters (location soon)
	 * 
	 * 		{
	 * 		"name" : "<chatroom_name>"
	 * 		}
	 * 
	 * ERROS:
	 * 	404 - Invalid URL resource access
	 * 	500 - Server-side error
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
			sendResponse(exchange, 404, null);
			return;
		}
	}
	
	/**
	 * Handles dispatching for a GET request to the server from the client
	 */
	private void handleGet(HttpExchange exchange) {
		System.out.println("Get request received");
		String[] path = exchange.getRequestURI().getPath().split("/");
		
		if (path.length == 0) {
			System.err.println("Get request with empty URL path");
			sendResponse(exchange, 404, null);
			return;
		}
		
		if (path[1].equalsIgnoreCase("chatroom")) {
			String query = exchange.getRequestURI().getQuery();
			
			if (query == null) {
				// Must be accessing all chatrooms API if no query string provided here.
				getChatrooms(exchange);
			} else {
				String[] chatIdField = query.split("=");
				
				if (chatIdField[0].equals("id")) {
					int chatId = Integer.parseInt(chatIdField[1]);
					getMessages(exchange, chatId);
				}
			}
		} else {
			// Invalid URL access
			sendResponse(exchange, 404, null);
		}
	}
	
	/**
	 * Handles dispatching for a POST request to the server from the client
	 */
	private void handlePost(HttpExchange exchange) {
		String[] path = exchange.getRequestURI().getPath().split("/");
		
		if (path.length == 0) {
			System.err.println("Post request with empty URL path");
			sendResponse(exchange, 404, null);
			return;
		}
		
		if (path[1].equalsIgnoreCase("chatroom")) {
			if (path.length == 3 && path[2].equalsIgnoreCase("create")) {
				createChatroom(exchange);
			} else {
				postMessage(exchange);
			}
		} else {
			sendResponse(exchange, 404, null);
		}
	}
	
	/**
	 * Gets all the messages associated with a particular chatID. These
	 * messages will be returned directly via the HttpExchange object.
	 */
	private void getMessages(HttpExchange exchange, int chatID) {
		DatabaseInteraction dbi = new DatabaseInteraction();
		List<Message> messages;
		
		try {
			dbi.open();
			messages = dbi.getMessages(chatID);
			dbi.close();
		} catch (Exception e) {
		  System.err.println("Unable to open database connection: " + e.getMessage());
		  sendResponse(exchange, 500, null);
		  return;
		}
		
		String jsonResponse = new Gson().toJson(messages);
		sendResponse(exchange, 200, jsonResponse);
	}
	
	/**
	 * Gets all the chatrooms (for a location and radius later). Will be returned
	 * directly to calling client.
	 */
	private void getChatrooms(HttpExchange exchange) {
		System.out.println("Getting chatrooms");
		List<Chatroom> chatrooms;
		
		try {
			DatabaseInteraction dbi = new DatabaseInteraction();
			dbi.open();
			chatrooms = dbi.getChatrooms();
			dbi.close();
		} catch (Exception e) {
		  // Handle
		  System.err.println("Unable to open database connection: " + e.getMessage());
		  sendResponse(exchange, 500, null);
		  return;
		}
		
		String jsonResponse = new Gson().toJson(chatrooms);
		sendResponse(exchange, 200, jsonResponse);
	}

	/**
	 * Creates a chatroom adhering to the parameters specified in the HTTP message body.
	 */
	private void createChatroom(HttpExchange exchange) {
		String json = extractRequestBody(exchange);
		if (json == null) {
			sendResponse(exchange, 500, null);
			return;
		}
		
		Chatroom chatroom = new Gson().fromJson(json, Chatroom.class);
		
		try {
			DatabaseInteraction dbi = new DatabaseInteraction();
			dbi.open();
			dbi.createChatroom(chatroom);
			dbi.close();
		} catch (Exception e) {
			System.err.println("Error creating chatroom: " + e.getMessage());
			sendResponse(exchange, 500, null);
			return;
		}
		
		System.out.println("Chatroom created");
		sendResponse(exchange, 200, null);
	}
	
	/**
	 * Posts a message to a particular chatroom.
	 */
	private void postMessage(HttpExchange exchange) {
		String json = extractRequestBody(exchange);
		if (json == null) {
			sendResponse(exchange, 500, null);
			return;
		}
		
		Message message = new Gson().fromJson(json, Message.class);
		
		try {
			DatabaseInteraction dbi = new DatabaseInteraction();
			dbi.open();
			dbi.addMessage(message);
			dbi.close();
		} catch (Exception e) {
			System.err.println("Error inserting message into database: " + e.getMessage());
			sendResponse(exchange, 500, null);
			return;
		}
		
		System.out.println("Message sent to database");
		sendResponse(exchange, 200, null);
	}
	
	/** 
	 * Extracts the HTTP request body associated with a given HttpExchange object.
	 * 
	 * @return The request body as a String or null if an error occurs trying to 
	 * extract the body.
	 */
	private static String extractRequestBody(HttpExchange exchange) {
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
			System.err.println("Error reading request body from client: " + e.getMessage());
			return null;
		}
		
		return buffer.toString();
	}
	
	/**
	 * Attempts to send a response.
	 * 
	 * @param exchange The exchange representing this HTTP connection.
	 * @param responseCode The desired response code to be sent.
	 * @param responseBody The response body. If no response is desired this should be null.
	 */
	private static void sendResponse(HttpExchange exchange, int responseCode, String responseBody) {
		try {
			// If response null we can just send the response code and call it a day.
			if (responseBody == null) {
				exchange.sendResponseHeaders(responseCode, -1);
				return;
			}
			
			// There must be a response to send so we do a bit more work.
			exchange.sendResponseHeaders(responseCode, responseBody.getBytes().length);
	
			OutputStream os = exchange.getResponseBody();
			os.write(responseBody.getBytes(), 0, responseBody.getBytes().length);
			os.close();
		} catch (IOException e) {
			System.err.println("Unable to send response: " + e.getMessage());
		}
	}
}
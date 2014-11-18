package Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.databaseOperations;

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
		String[] path = exchange.getRequestURI().getPath().split("/");
		
		if (path.length == 0) {
			System.err.println("Empty URL path");
			return;
		}
		
		int chatId;
		if (path[0].equalsIgnoreCase("chatroom")) {
			chatId = Integer.parseInt(path[1]);
		}
		
		// TODO: Query DB for messages in with chatroom_id = 'chatId'
		ResultSet rs = null;
		try {
			rs = databaseOperations.readMessage(databaseOperations.getConnection());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		String jsonResponse = "{messages : [";
		// TODO: Iterate through rows and build up JSON response
		try {
			while (rs.next()) {
				String id = rs.getString("ID");
				String text = rs.getString("text");
				String timestamp = rs.getString("timestamp");
				String message = "{ username : noone, timestamp : " + timestamp + ", message : " + text + " },";
				jsonResponse += message;
			}
			jsonResponse += "]}";
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
			
			OutputStream os = exchange.getResponseBody();
			os.write(jsonResponse.getBytes().length);
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
		
		// TODO: Parse requestBody JSON
		// TODO: Update DB to hold the message attached in the POST
	}
}

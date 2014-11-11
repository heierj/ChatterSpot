package Server;

import java.io.IOException;
import java.io.OutputStream;

//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;


/** 
 * Handles RESTful HTTP requests from the client.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
public class ClientHandler { //implements HttpHandler {

	/**
	 * {@inheritDoc}
	 */
/*	public void handle(HttpExchange exchange) throws IOException {
		String response = "Resource requested: \"" + exchange.getRequestURI().toString() + "\"\n" +
				"Request method: " + exchange.getRequestMethod();

		exchange.sendResponseHeaders(200, response.getBytes().length);
		
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}*/
}

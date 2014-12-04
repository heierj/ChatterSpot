package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ForkJoinPool;

import com.sun.net.httpserver.HttpServer;


/** 
 * Class ServerMain is a RESTful back-end server application for ChatterSpot.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */

public class ServerMain {
	private static final int PORT_NUM = 4444;
	
	/**
	 * Begin running the ChatterSpot server
	 */
	public static void main(String[] args) {
		HttpServer server = null;
		
		try {
			server = HttpServer.create(new InetSocketAddress(PORT_NUM), 0);
		} catch (IOException e) {
			System.err.println("Unable to create server on port: " + PORT_NUM);
			System.exit(1);
		}
		
		server.setExecutor(new ForkJoinPool());
		
		// Set a default handler to capture and handle every URI.
		server.createContext("/", new ClientHandler());
		
		server.start();
	}
}

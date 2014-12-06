package Client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractClient {
	static final String SERVER_URL = "http://173.250.159.172:4447";
	
	/**
	 * Given a string of the URL to connect to, this function will open the
	 * connection. Sets connection to be both input and output
	 * @param connectTo the string of the URL to connect to
	 * @return the opened connection
	 */
	static HttpURLConnection openConnection(String connectTo, boolean doOutput, boolean doInput) {
		// Create the URL to send request to
		URL url;
		try {
			url = new URL(connectTo);
		} catch (MalformedURLException e1) {
			System.err.println("URL is invalid: " + connectTo);
			return null;
		}

		// Set up the HTTP
		HttpURLConnection client;
		try {
			client = (HttpURLConnection) url.openConnection();
			client.setDoOutput(doOutput);
			client.setDoInput(doInput);
		} catch (IOException e) {
			System.err.println("Could not open HTTP connection to: "
					+ connectTo);
			return null;
		}

		return client;
	}
}

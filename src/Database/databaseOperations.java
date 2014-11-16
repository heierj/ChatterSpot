package Database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseOperations {
	private static Connection getConnection() throws URISyntaxException, SQLException {
	    URI dbUri = new URI("postgres://wmocemjkyothtq:7ta5V10mOt12ObvORMQExVSicx@ec2-54-83-204-244.compute-1.amazonaws.com:5432/d9jfi2n6fi73a2");
	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
	    try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return DriverManager.getConnection(dbUrl, username, password);
	}
	
	private static void addMessage(Connection dbConnection, String message) throws SQLException {
		String insertTableSQL = "INSERT INTO messages"
				              + "(text, timestamp) VALUES" + "( '" + message + "', 'now')";
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();
			System.out.println(insertTableSQL);

			// execute insert SQL statement
			statement.executeUpdate(insertTableSQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(statement != null) {
				statement.close();
			}
		}
	}
	
	private static void readMessage(Connection dbConnection, int i) throws SQLException {
		String selectTableSQL = "SELECT id, text, timestamp from messages";
		Statement statement = null;
		try {
				statement = dbConnection.createStatement();

				ResultSet rs = statement.executeQuery(selectTableSQL);
				 
				while (rs.next()) {
	 
					String userid = rs.getString("ID");
					String username = rs.getString("text");
					String timestamp = rs.getString("timestamp");
	 
					System.out.println("ID : " + userid);
					System.out.println("text : " + username);
					System.out.println("timestamp : " + timestamp);
	 
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(statement != null) {
				statement.close();
			}
}
		
	}
	
	public static void main(String[] args) {
		try {
			Connection db = getConnection();
			addMessage(db, "hello");
			readMessage(db, 3);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

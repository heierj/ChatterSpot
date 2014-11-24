package Database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseOperations {
	public static Connection getConnection() throws URISyntaxException, SQLException {
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
	
	public static void addMessage(Connection dbConnection, String message) throws SQLException {
		String insertTableSQL = "INSERT INTO messages"
				              + "(text, timestamp) VALUES" + "( '" + message + "', 'now')";
		Statement statement = null;
		try {
			statement = dbConnection.createStatement();;

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
	
	public static ResultSet readMessage(Connection dbConnection) throws SQLException {
		String selectTableSQL = "SELECT id, text, timestamp from messages";
		Statement statement = null;
		try {
				statement = dbConnection.createStatement();

				ResultSet rs = statement.executeQuery(selectTableSQL);
				 
				return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(statement != null) {
				statement.close();
			}
		}
		return null;
		
	}
}

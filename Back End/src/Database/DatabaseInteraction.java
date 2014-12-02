package Database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import Shared.Message;
import Shared.Chatroom;

public class DatabaseInteraction {
  private static final String DB_URI = "postgres://wmocemjkyothtq:7ta5V10mOt12ObvORMQExVSicx@ec2-54-83-204-244." + 
        "compute-1.amazonaws.com:5432/d9jfi2n6fi73a2";
  
  private Connection dbConnection;
  
  /**
   * Opens a connection to the ChatterSpot database
   */
  public void open() throws URISyntaxException, SQLException {
    URI dbUri = new URI(DB_URI);
    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + 
          dbUri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
    dbConnection = DriverManager.getConnection(dbUrl, username, password);
  }
  
  /**
   * Closes this connection if it is currently open
   */
  public void close() throws SQLException {
    if (dbConnection == null) {
      dbConnection.close();
    }
  }
  
  /**
   * Adds a message to the database
   */
  public void addMessage(Message message) throws SQLException {
    // For now we ingore the timestamp, username, and chatroomID components of the Message
    String insertTableSQL = 
        "INSERT INTO messages" + 
        "(text, chatroom_id, user_name, timestamp) VALUES" + "( '" + message.getMessage() + "', '" + 
        		message.getChatNumber() + "', '" + message.getUsername() + "', 'now')";
    
    Statement statement = dbConnection.createStatement();
    statement.executeUpdate(insertTableSQL);
    statement.close();
  }
  
  /**
   * Gets all the current messages within a given chatroom
   * 
   * @param chatroomID The chat id from which the messages should be retrieved
   * @return A list of messages from the chatroom
   */
  public List<Message> getMessages(int chatroomID) throws SQLException {
    List<Message> messages = new ArrayList<Message>();
    
    String selectTableSQL = "SELECT id, text, timestamp, user_name from messages WHERE chatroom_id =" + chatroomID
    		                + " ORDER BY timestamp;";
    Statement statement = dbConnection.createStatement();
    ResultSet rs = statement.executeQuery(selectTableSQL);
    
    while (rs.next()) {
      String text = rs.getString("text");
      String timestamp = rs.getString("timestamp");
      String username = rs.getString("user_name");
      
      // Until later implemented, all usernames are "nousername" and chatroom ids
      // are all 1.
      Message message = new Message(username, text, Timestamp.valueOf(timestamp), chatroomID);
      messages.add(message);
    }
    statement.close();
    
    return messages;
  }
  
  //should take in gps coords
  public void createChatroom(String name) throws SQLException {
	    String insertTableSQL = 
	        "INSERT INTO chatrooms" + 
	        "(name, timestamp) VALUES" + "( '" + name + "', 'now')";
	    
	    Statement statement = dbConnection.createStatement();
	    statement.executeUpdate(insertTableSQL);
	    statement.close();
  }
  
  //should eventually take gps coords
  public List<Chatroom> getChatrooms() throws SQLException {
	  ArrayList<Chatroom> chatrooms = new ArrayList<Chatroom>();
	    
	    String selectTableSQL = "SELECT id, name, timestamp from chatrooms";
	    Statement statement = dbConnection.createStatement();
	    ResultSet rs = statement.executeQuery(selectTableSQL);
	    while (rs.next()) {
	      int id = rs.getInt("id");
	      String name = rs.getString("name");
	      String timestamp = rs.getString("timestamp");
	      Chatroom chatroom = new Chatroom(name, Timestamp.valueOf(timestamp), id);
	      chatrooms.add(chatroom);
	    }
	    statement.close();
		return chatrooms;
  }
  
}

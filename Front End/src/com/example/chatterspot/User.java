package com.example.chatterspot;

/**
 * Singleton class for using username between different 
 * activities
 */
public class User {
	private String username;
	private static User user = new User();
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public static User getInstance() {
		return user;
	}
	
	public String getUsername() {
		return username;
	}
	
}

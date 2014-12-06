package com.example.chatterspot;

import com.google.android.gms.maps.model.Marker;

import Shared.Chatroom;

public class SpotMarker {
	
	public Chatroom chatroom;
	public Marker marker;
	
	public SpotMarker(Chatroom chatroom, Marker marker) {
		this.chatroom = chatroom;
		this.marker = marker;
	}	
}

package Utils;

import java.util.Comparator;

import Shared.Chatroom;

/**
 * Compares two chat rooms and orders them by distance (Smallest to largest).
 */
public class ChatroomComparator implements Comparator<Chatroom>{
	@Override
	public int compare(Chatroom lhs, Chatroom rhs) {
		return (int) (lhs.getCurDist() - rhs.getCurDist());
	}
}

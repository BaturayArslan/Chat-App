package server;

import java.util.HashMap;

import message.Message;

public class RoomManager {
	private HashMap<Integer, Room> rooms;
	private Server server;
	private int counter;
	
	public RoomManager(Server server ) {
		rooms = new HashMap<Integer,Room>();
		this.server = server;
		counter = 0;
		// Default room for those haven't been joined room and waiting on loby.
		rooms.put(0,new Room(0,"NO_ROOM", 10000, true, ""));
	}
	
	public synchronized Room createRoom(String name, int maxUser, boolean isPublic, String password) {
		counter += 1;
		Room newRoom = new Room(counter, name, maxUser, isPublic, password );
		rooms.put(counter, newRoom);
		return newRoom;
	}
	
	public synchronized void deleteRoom(int id) {
		if(rooms.containsKey(id)) {
			rooms.remove(id);
		}
	}
	
	public synchronized boolean InRooms(String name) {
		for(Room room : rooms.values()) {
			if(room.inRoom(name)) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized Room getLoby() {
		return rooms.get(0);
	}
	
	public synchronized Room enterLoby(Client client) {
		Room lobyRoom = rooms.get(0);
		lobyRoom.addUser(client);
		return lobyRoom;
	}
	
	public synchronized void sendLoby(Message message) {
		Room lobyRoom = rooms.get(0);
		lobyRoom.sendAll(message);
	}
	
	public synchronized Room findUserRoom(String name) {
		for(Room room : rooms.values()) {
			if(room.inRoom(name)) {
				return room;
			}
		}
		return null;
	}
	
	public synchronized Room getRoom(int id) {
		return rooms.get(id);
	}
	
	public synchronized Room joinRoom(int id ,String password ,Client client) {
		// if room reached max user then this function returns null
		Room room = rooms.get(id);
		if( room.isValid(password) && room.addUser(client)) {
			return room;
		}
		return null;
	}
	
	public synchronized void leaveRoom(int id, Client client) {
		Room room = rooms.get(id);
		room.leave(client.getName());
	}
	
	public synchronized Room[] getAllRoom() {
		Room[] roomList = new Room[rooms.size() - 1];
		int j = 0;
		for(Room room: rooms.values()) {
			if(room.getID() != 0) {
				roomList[j] = room;				
				j += 1; 
			}
		}
		return roomList;
	}
	
}

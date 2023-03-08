package server;

import java.util.HashMap;

import message.Message;

public class Room {
	private int id;
	private String name;
	private int maxUser;
	private boolean isPublic;
	private String password;
	private Client admin;
	private HashMap<String, Client> users;
	
	public Room(int id,String name, int maxUser, boolean isPublic, String password) {
		this.name = name;
		this.maxUser = maxUser;
		this.isPublic = isPublic;
		this.password = password;
		this.id = id;
		this.users = new HashMap<String,Client>();
		
	}
	
	public int getMaxUser() {
		return maxUser;
	}
	
	public boolean isValid(String password) {
		if(isPublic) {
			return true;
		}
		if(this.password.equals(password)) {
			return true;
		}else {
			return false;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPublic() {
		return isPublic;
	}
	
	public int getID() {
		return id;
	}
	
	public void setAdmin(Client admin) {
		this.admin = admin;
	}
	
	public Client getAdmin() {
		return this.admin;
	}
	
	public synchronized boolean inRoom(String name) {
		return users.containsKey(name);
	}
	
	public synchronized boolean  addUser(Client client) {
		if(users.size() + 1 <= maxUser) {
			users.put(client.getName(), client);
			return true;
		}
		return false;
	}
	
	public synchronized void sendAll(Message message) {	
		for(Client users: users.values()) {
			users.send(message);
		}
	}
	
	public synchronized void leave(String name) {
		if(users.containsKey(name)) {
			Client client = users.get(name);
			users.remove(name);
		}
	}
	
	public int getSize() {
		return users.size();
	}
	
	public synchronized Client[] getUsers() {
		Client[] userList = new Client[users.size()];
		int i = 0;
		for(Client user: users.values()) {
			userList[i] = user;
			i += 1;
		}
		
		return userList;
	}
	
}

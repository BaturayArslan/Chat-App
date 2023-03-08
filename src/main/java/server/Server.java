package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.UUID;

import message.Message;

public class Server implements Runnable{
	private int port;
	private boolean isRunning;
	private ServerSocket socket;
	private DbManager db;
	private RoomManager roomManager;
	private HashMap<UUID,Client> clients;

	private Thread runThread;
	
	public static int MAX_ATTEMPT = 5;
	
	public Server(int port) {
		this.port = port;
		this.isRunning = false;
		this.clients = new HashMap<UUID,Client>();
		this.db = new DbManager();
		this.roomManager = new RoomManager(this);
		db.connect();
 
		
		try {
			socket = new ServerSocket(port);
			runThread = new Thread(this ,"Run Thread");
			runThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public synchronized void disconnect(UUID id) {
		if(clients.containsKey(id)) {
			Client client = clients.get(id);
			String name = client.getName() == null ? "Ananymous" : client.getName();
			System.out.println(name + " disconnected from " + client.getAddress().toString() + ":" + client.getPort());
			clients.remove(id);
		}
	}
	
	
	public int getPort() {
		return port;
	}
	
	public Connection getDb() throws NullPointerException {
		return db.getConnection();
	}
	
	public void run() {
		isRunning = true;
		
		while(isRunning) {
			try {
				Socket clientSocket = socket.accept();
				UUID id = UUID.randomUUID();
				Client newClient = new Client(clientSocket, this, id);
				synchronized (this) {
					clients.put(id, newClient);					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				isRunning = false;
			}
		}
	}
	
	public Room createRoom(String name, int maxUser, boolean isPublic, String password) {
		return roomManager.createRoom(name, maxUser, isPublic, password);
	}
	
	public RoomManager getRoomManager() {
		return roomManager;
	}
	
	public synchronized void removeClient(Client client) {
		if(clients.containsKey(client.getId())) {
			clients.remove(client.getId());
		}
	}

}

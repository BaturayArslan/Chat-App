package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
	private HashMap<String,Connection> connections;
	
	private static final String ADDRESS = "localhost";
	private static final int PORT = 8181;

	
	public ConnectionManager() {
		this.connections = new HashMap<String, Connection>();
	}
	
	public Connection makeConnection(String username) throws IOException {
		Connection newConnection = new Connection(ADDRESS, PORT);
		connections.put(username, newConnection);
		return newConnection;
			
	}
	
	public void clear(String username) {
		if(connections.containsKey(username)) {
			connections.remove(username);
		}
	}
	
	
	public Connection getConnection(String username) {
		return connections.get(username);
	}
	
	public String getAddress() {
		return ADDRESS;
	}
	
	public int getPort() {
		return PORT;
	}
	
	
}

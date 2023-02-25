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

	
	public ConnectionManager() {
		this.connections = new HashMap<String, Connection>();
	}
	
	public Connection makeConnection(String username,String address, int port) {
		try {
			Connection newConnection = new Connection(username, address, port);
			connections.put(username, newConnection);
			return newConnection;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public Connection getConnection(String username) {
		return connections.get(username);
	}
	
	
}

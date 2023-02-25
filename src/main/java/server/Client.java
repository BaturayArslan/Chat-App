package server;

import java.net.InetAddress;
import java.util.UUID;

public class Client {
	private String username;
	private InetAddress ip;
	private int port;
	private int attempt;
	private UUID ID; 
	
	public Client(String username, InetAddress ip, int port, UUID id) {
		this.username = username;
		this.ip = ip;
		this.port = port;
		this.attempt = 0;
		this.ID = id;
	}
	
	public String toString() {
		return "{ " + username + " from "+ip.getHostAddress() + ":"+ port + " and id is " + ID.toString() + " }";
	}
	
	public int getPort() {
		return this.port;
	}
	
	public InetAddress getAddress() {
		return this.ip;
	}
	
}

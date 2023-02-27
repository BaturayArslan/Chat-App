package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class Connection {

	private InetAddress ip;
	private int port;
	private String username;
	private DatagramSocket socket;
	private boolean active;
	
	public Connection(String username,String address, int port) throws Exception {
		try {
			this.username = username;
			this.ip = InetAddress.getByName(address);
			this.port = port;
			this.socket = new DatagramSocket();
			this.active = true;


			
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public String recieve() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
			return new String(packet.getData(),packet.getOffset(),packet.getLength());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public void send(final String message) {
		final byte[] data = message.getBytes();
		Thread sendThread = new Thread("sendThread") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data,data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}

	
	
	public String getUsername() {
		return username;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getAddress() {
		return ip.getHostAddress();
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	 public void setActive(boolean value) {
		 this.active = value;
	 }
	

	

	
	
	
}

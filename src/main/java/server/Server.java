package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Server {
	private int port;
	private DatagramSocket socket;
	private boolean isRunning;
	private HashMap<String,Client> clients;
	private HashSet<String> responded;
	private Thread receiveThread, clientManThread;
	
	public static int MAX_ATTEMPT = 5;
	
	public Server(int port) {
		this.port = port;
		this.isRunning = false;
		this.clients = new HashMap<String,Client>();
		this.responded = new HashSet<String>();
		try {
			socket = new DatagramSocket(port);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	

	
	private void manageClient() {
		clientManThread = new Thread("clientMan Thread") {
			public void run() {
				while(isRunning) {
					ping();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					checkClient();
				}					
			}
		};
		clientManThread.start();
	}
	
	
	private void receive() {
		receiveThread = new Thread("receive Thread") {
			public void run() {
				byte[] data = new byte[1024];
				DatagramPacket packet = new DatagramPacket(data,data.length);
				while(isRunning) {
					try {
						socket.receive(packet);
						process(packet);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}					
				
			}
		};
		receiveThread.start();
	}
	
	private  synchronized void process(DatagramPacket packet) {
		String message =new String(packet.getData(),packet.getOffset(),packet.getLength());
		if(message.startsWith("/c/")) {
			// Client want to connect connect client and inform client --> "/c/<username>"
			UUID id = UUID.randomUUID();
			String name = message.substring(3,message.length());
			if(clients.containsKey(name)) {
				// TODO : Error
				return;
			}
			clients.put(name, new Client(name,packet.getAddress(),packet.getPort(),id));
			send(message, packet.getAddress(), packet.getPort());
			System.out.println(name + " connected from " + packet.getAddress().toString()+ ":" + packet.getPort());
		}else if (message.startsWith("/m/")) {
			// Client want to send message send messages all other client -> "/m/<message>"
			sendAll(message);
		}else if (message.startsWith("/d/")) {
			// Client want to disconnect with message -> "/d/<username>"
			String name = message.substring(3, message.length());
			disconnect(clients.get(name), false);
		}else if(message.startsWith("/pong/")) {
			// Client response back to our ping message
			String name = message.substring(6, message.length());
			responded.add(name);
		}
		else {
			System.out.println(message);
		}
	}
	
	private synchronized void sendAll(String message) {
		
		for(Client client: clients.values()) {
			send(message, client.getAddress(),client.getPort());
		}
	}
	
	private void send(String message, final InetAddress address, final int port) {
		final byte[] data = message.getBytes(); 
		Thread send = new Thread(){
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length,address,port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	private synchronized void disconnect(Client client, boolean isTimeout) {
		if(client != null) {
			clients.remove(client.getName());
			String cause = isTimeout ? " timeout from ":" disconnected from ";
			System.out.println(client.getName() + cause + client.getAddress().toString() + ":" + client.getPort());
		}
	}
	
	public int getPort() {
		return port;
	}
	
	public void start() {
		isRunning = true;
		manageClient();
		receive();
	}
	
	private synchronized void ping() {
		for(Client client : clients.values()) {
			send("/ping/" + client.getName(), client.getAddress(),client.getPort());
		}
	}
	
	private synchronized void checkClient() {
		for(Client client: clients.values()) {
			if(!responded.contains(client.getName())) {
				client.incAttempt();
				if(client.getAttempt() > MAX_ATTEMPT) {
					disconnect(client, true);
				}
			}else {
				responded.remove(client.getName());
				client.resetAttempt();
			}
			
		}
	}

}

package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;

public class Server implements Runnable {
	private int port;
	private DatagramSocket socket;
	private boolean isRunning;
	private HashMap<String,Client> clients;
	
	private Thread run,sendThread, receiveThread, clientManThread;
	
	public Server(int port) {
		this.port = port;
		this.isRunning = false;
		this.clients = new HashMap<String,Client>();
		try {
			socket = new DatagramSocket(port);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
		run = new Thread(this,"Server:"+ port + " Thread");
		run.start();
	}
	
	public void run() {
		isRunning = true;
		manageClient();
		receive();
		
	}
	
	private void manageClient() {
		clientManThread = new Thread("clientMan Thread") {
			public void run() {
				while(true) {
					
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
				while(true) {
					try {
						socket.receive(packet);
						process(packet);
						System.out.println(new String(packet.getData(),packet.getOffset(),packet.getLength()));
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		receiveThread.start();
	}
	
	public void process(DatagramPacket packet) {
		String message =new String(packet.getData(),packet.getOffset(),packet.getLength());
		if(message.startsWith("/c/")) {
			UUID id = UUID.randomUUID();
			String name = message.substring(3,message.length());
			if(clients.containsKey(name)) {
				// TODO : Error
				return;
			}
			clients.put(name, new Client(name,packet.getAddress(),packet.getPort(),id));
		}else if (message.startsWith("/m/")) {
			sendAll(message);
		}
		else {
			System.out.println(message);
		}
	}
	
	public void sendAll(String message) {
		
		for(Client client: clients.values()) {
			send(message, client.getAddress(),client.getPort());
		}
	}
	
	public void send(String message, final InetAddress address, final int port) {
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


}

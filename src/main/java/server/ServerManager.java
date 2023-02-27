package server;

import java.util.ArrayList;

public class ServerManager {
	
	private ArrayList<Server> servers;
	
	public ServerManager() {
		servers = new ArrayList<Server>();
		
	}
	
	public void addServer(Server server) {
		servers.add(server);
	}
	
	public void startServer(int port) {
		for(Server server: servers) {
			if(server.getPort() == port) {
				server.start();
				return;
			}
		}
	}
	public void startServer() {
		for(Server server: servers) {
			server.start();
		}
	}
	
	public static void main(String[] args) {
		
		if(args.length != 1 ) {
			System.out.println("Please enter port.");
			return;
		}
		
		int port = Integer.parseInt(args[0]);
		System.out.println(port  + " specified as a new server port.");
		
		ServerManager manager = new ServerManager();
		Server server = new Server(port);
		manager.addServer(server);
		manager.startServer();
		
	}
}

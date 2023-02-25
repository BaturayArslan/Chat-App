package client;

import java.net.DatagramSocket;

import javax.swing.JFrame;

public class WindowManager {
	private JFrame loginPage;
	private JFrame chatPage;
	private ConnectionManager connManager;
	
	public WindowManager(ConnectionManager connManager) {		
		try {
			this.connManager = connManager;
			loginPage = new Login(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setChatPage(JFrame chatFrame) {
		this.chatPage = chatFrame;
	}
	
	public JFrame getChatPage() {
		return chatPage;
	}
	
	public ConnectionManager getConnManager() {
		return connManager;
	}
	
	public void activateChat(String username, String address, int port) {
		Connection connection = connManager.makeConnection(username, address, port);
		chatPage = new Chat(username,connection);
	}
	
	public static void main(String[] args) {
		ConnectionManager connManager = new ConnectionManager();
		WindowManager windowManager = new WindowManager(connManager);
	}
}

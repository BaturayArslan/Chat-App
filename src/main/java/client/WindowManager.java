package client;

import java.io.IOException;
import java.net.DatagramSocket;

import javax.swing.JFrame;

public class WindowManager {
	private JFrame loginPage;
	private JFrame chatPage;
	private ConnectionManager connManager;
	
	public WindowManager(ConnectionManager connManager) {	
		this.connManager = connManager;
		activateLogin();
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
	
	public void activateChat(String username) {
		chatPage = new Chat(username,connManager.getConnection("init"),this);
	}
	
	public void activateRoom(String username) {
		Room roomPage = new Room(username, connManager.getConnection("init"), this);
	}
	
	public synchronized void activateLogin() {
		connManager.clear("init");
		boolean isError = false;
		try {
			connManager.makeConnection("init");
			
		} catch (IOException e) {
			// Connection couldn't established.
			isError = true;
		}finally {			
			loginPage = new Login(this,connManager.getConnection("init"));
			if(isError) {
				((Login) loginPage).showError("Connection Couldn't Established.Please Try Again.");				
			}
		}
	}
	
	
	public void reconnect() {
		if(connManager.getConnection("init") == null ) {
			try {
				Connection connection = connManager.makeConnection("init");
				((Login) loginPage).setConnection(connection);
				
			} catch (IOException e) {
				// Connection couldn't established.
				((Login) loginPage).showError("Connection Couldn't Established.Please Try Again.");
			}
		}
	}
	
}

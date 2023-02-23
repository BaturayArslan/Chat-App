package client;

import javax.swing.JFrame;

public class WindowManager {
	private JFrame loginPage;
	private JFrame chatPage;
	
	public WindowManager() {		
		try {
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
	
	public void activateChat(String username, String address, int port) {
		chatPage = new Chat(username, address, port);
	}
	
	public static void main(String[] args) {
		WindowManager manager = new WindowManager();
	}
}

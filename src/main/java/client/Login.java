package client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import message.Message;
import message.MessageType;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtName;
	private WindowManager windowManager;
	private JTextField txtPassword;
	private JPasswordField passwordField;
	
	boolean active;
	Connection connection;
	Thread receiveThread;
	
	
	public Login(WindowManager manager, Connection connection) {
		// Connection will be null if connection couldn't established.
		
		this.windowManager = manager;
		this.connection = connection;
		active = true;
		
		if(connection != null) {
			startThread();
		}
		
		createWindow();
		
	}
	
	private void createWindow(){	
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setBounds(100, 100, 361, 444);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(79, 116, 187, 20);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUsername.setBounds(81, 93, 91, 14);
		contentPane.add(lblUsername);
		
		txtPassword = new JPasswordField();
		txtPassword.setColumns(10);
		txtPassword.setBounds(79, 173, 187, 20);
		contentPane.add(txtPassword);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPassword.setBounds(81, 147, 91, 14);
		contentPane.add(lblPassword);
		
		
		JPanel panel = new JPanel();
		panel.setBounds(79, 215, 187, 42);
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JButton btnConnect = new JButton("Connect");
		panel.add(btnConnect);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
				
			}
		});

		
		panel.add(Box.createHorizontalGlue());
		
		JButton btnRegister = new JButton("Register");
		panel.add(btnRegister);
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				register();
				
			}
		});
		
		
		setVisible(true);
		
	}
	
	private void login() {
		String userName = txtName.getText();
		String password = txtPassword.getText();
		Message message = new Message(2,userName + "," + password);
		connection.send(message);	
	}
	
	private void register() {
		String userName = txtName.getText();
		String password = txtPassword.getText();
		Message message = new Message(8,userName + "," + password);
		connection.send(message);
		
	}
	
	public void showError(String error) {
		JDialog dialog = new ReconnectionDialog(error, this);
	}
	
	public WindowManager getWindowManager() {
		return windowManager;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
		if(receiveThread == null) {
			startThread();
		}
	}
	
	public void startThread() {
		receiveThread = new Thread() {
			public void run() {
				while(active) {
					try {
						Message message = connection.recieve();
						process(message);
					} catch (IOException | InterruptedException e) {
						// Broken connection
						active = false;
						e.printStackTrace();
					}					
				}
			}
		};
		receiveThread.setName("Login Receive Thread");
		receiveThread.start();
	}
	
	public void process(Message message) {
		if(message.getMessageCode() == MessageType.ERROR.getCode()) {
			JDialog dialog = new ErrorDialog(message.getMessage());
		}else if (message.getMessageCode() == MessageType.LOGIN_SUCCESS.getCode()) {
			active=false;
			dispose();
			windowManager.activateRoom(message.getMessage());
		}else if (message.getMessageCode() == MessageType.REGISTER_SUCCESS.getCode()) {
			JDialog dialog = new ErrorDialog(message.getMessage());
		}else {
			System.out.println("Unknow message type: " + message.getMessage());
		}
	}
}

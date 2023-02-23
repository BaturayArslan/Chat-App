package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JTextField txtPort;
	private WindowManager windowManager;
	
	public Login(WindowManager manager) {
		this.windowManager = manager;
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
		
		txtAddress = new JTextField();
		txtAddress.setColumns(10);
		txtAddress.setBounds(79, 173, 187, 20);
		contentPane.add(txtAddress);
		
		JLabel lblAddress = new JLabel("Ip Address");
		lblAddress.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblAddress.setBounds(81, 147, 91, 14);
		contentPane.add(lblAddress);
		
		txtPort = new JTextField();
		txtPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				}
			}
		});
		txtPort.setColumns(10);
		txtPort.setBounds(79, 231, 187, 20);
		contentPane.add(txtPort);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPort.setBounds(81, 210, 91, 14);
		contentPane.add(lblPort);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
				
			}
		});
		btnConnect.setBounds(128, 275, 89, 23);
		contentPane.add(btnConnect);
		
		setVisible(true);
		
	}
	
	private void login() {
		String userName = txtName.getText();
		String address = txtAddress.getText();
		int port = Integer.parseInt(txtPort.getText());
		System.out.println("Username: " + userName + ", address: " + address +  ", port: " + port);
		dispose();
		windowManager.activateChat(userName, address, port);
		
	}
	


}

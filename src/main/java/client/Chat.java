package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.Insets;
import java.awt.ScrollPane;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.DatagramSocket;

public class Chat extends JFrame {
	
	private String username;
	private Thread consumeThread;
	
	private JPanel contentPane;
	private Connection connection;
	private JTextField txtMessage;
	private JTextArea txtrMessages;
	private DefaultCaret carret;
	
	public Chat(String username, Connection connection) {
		this.username = username;
		this.connection = connection;
		
		createWindow();
		print("Trying to connect as  " + username  );
		
		
		if(connection != null){
			// connection established
			print("Connection establised");
			consumeThread = new Thread() {
				public void run() {
					consumeReceived();
				}
			};
			consumeThread.setName("Chat Consume Thread");
			consumeThread.start();
			connection.send("/c/" + username);
			connection.send("User:" + username + " connected from " 
				+ connection.getAddress() 
				+ ":" 
				+ connection.getPort()
			);
			
		}
	}
	
	
	private void createWindow() {	
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setVisible(true);
		setTitle("Chat Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200,700);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{1000, 75, 125};
		gbl_contentPane.rowHeights = new int[]{600,100};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0};
		contentPane.setLayout(gbl_contentPane);
		
		txtrMessages = new JTextArea();
		txtrMessages.setEditable(false);
		txtrMessages.setBackground(new Color(128, 128, 192));
		txtrMessages.setFont(new Font("Tahoma", Font.PLAIN, 12));
		carret = (DefaultCaret) txtrMessages.getCaret();
		GridBagConstraints gbc_txtrMessages = new GridBagConstraints();
		gbc_txtrMessages.insets = new Insets(0, 0, 5, 5);
		gbc_txtrMessages.gridx = 0;
		gbc_txtrMessages.gridy = 0;
		gbc_txtrMessages.gridwidth = 2;
		gbc_txtrMessages.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(txtrMessages);
		contentPane.add(scrollPane, gbc_txtrMessages);
		
		JPanel panelUsers = new JPanel();
		GridBagConstraints gbc_panelUsers = new GridBagConstraints();
		gbc_panelUsers.insets = new Insets(0, 0, 5, 5);
		gbc_panelUsers.fill = GridBagConstraints.BOTH;
		gbc_panelUsers.gridx = 2;
		gbc_panelUsers.gridy = 0;
		gbc_panelUsers.gridheight = 2; 
		contentPane.add(panelUsers, gbc_panelUsers);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					send();
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		txtMessage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		gbc_txtMessage.insets = new Insets(0, 0, 5, 5);
		gbc_txtMessage.fill = GridBagConstraints.BOTH;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 1;
		contentPane.add(txtMessage, gbc_txtMessage);
		
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.fill = GridBagConstraints.VERTICAL;
		gbc_btnSend.insets = new Insets(0, 0, 5, 5);
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 1;
		contentPane.add(btnSend, gbc_btnSend);
		
		txtMessage.requestFocusInWindow();
		
		
	}
	
	public void send() {
		String message = txtMessage.getText();
		txtMessage.setText("");
		if(!message.equals("")) {			
			txtrMessages.append(username + ": " + message + "\n\r");
			txtrMessages.setCaretPosition(txtrMessages.getDocument().getLength());
			connection.send("/m/" + message);
			
		}
	}
	
	public void print(String message) {
		txtrMessages.append(message + "\n\r");
	}
	
	private void consumeReceived() {
		String message;

		while(connection.isActive()) {
			synchronized (connection) {
				if(! connection.isMessage()) {
					try {
						System.out.println("queueu is empty");
						connection.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				message = connection.getReceivedMessage();
				txtrMessages.append(message);			
			}
		}
		
	}
	

}

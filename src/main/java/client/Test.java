package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class Test extends JFrame {

	private String username;
	private Thread receiveThread;
	private Connection connection;
	
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea txtrMessages;
	private DefaultCaret carret;
	private JLabel lblUserName1;
	private JButton btnLeave;
	

	public Test() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setTitle("Chat Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200,700);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{1000, 75, 125};
		gbl_contentPane.rowHeights = new int[]{50,550,100};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.5, 0.2};
		gbl_contentPane.rowWeights = new double[]{0.1,1.0, 0.0};
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
		gbc_txtrMessages.gridheight = 2;
		gbc_txtrMessages.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(txtrMessages);
		contentPane.add(scrollPane, gbc_txtrMessages);
		
		btnLeave = new JButton("Leave ");
		btnLeave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		GridBagConstraints gbc_btnLeave = new GridBagConstraints();
		gbc_btnLeave.fill = GridBagConstraints.BOTH;
		gbc_btnLeave.insets = new Insets(0, 0, 5, 0);
		gbc_btnLeave.gridx = 2;
		gbc_btnLeave.gridy = 0;
		contentPane.add(btnLeave, gbc_btnLeave);
		
		JPanel panelUsers = new JPanel();
		GridBagConstraints gbc_panelUsers = new GridBagConstraints();
		gbc_panelUsers.fill = GridBagConstraints.BOTH;
		gbc_panelUsers.gridx = 2;
		gbc_panelUsers.gridy = 1;
		gbc_panelUsers.gridheight = 2; 
		JScrollPane userScrollPane = new JScrollPane(panelUsers);
		contentPane.add(userScrollPane, gbc_panelUsers);
		panelUsers.setLayout(new BoxLayout(panelUsers, BoxLayout.Y_AXIS));
		
		lblUserName1 = new JLabel("Baturay");
		ImageIcon icon = new ImageIcon("C:\\Users\\lenovo\\Downloads\\yellow-msn.png");
		Image scaledImage = scaleImage(icon.getImage(), 20, 20);
		ImageIcon scaledIcon = new ImageIcon(scaledImage);
		lblUserName1.setIcon(scaledIcon);
		panelUsers.add(lblUserName1);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		txtMessage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtMessage.setColumns(10);
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.BOTH;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.fill = GridBagConstraints.BOTH;
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//connection.close();
			}
		});
		
		txtMessage.requestFocusInWindow();
		
		setVisible(true);
		
	}
	
	private Image scaleImage(Image icon, int width, int height) {
		Image scaled = icon.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return scaled;
	}
	
	public static void main(String[] args) {
		Test test = new Test();
	}

}

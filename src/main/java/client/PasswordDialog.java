package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PasswordDialog extends JDialog {
	private JPasswordField passwordField;

	public PasswordDialog(final Room container, final int id , final String roomName) {
		setTitle("Password");
		setBounds(100, 100, 494, 169);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPassword.setBounds(10, 25, 152, 37);
		getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(158, 32, 202, 20);
		getContentPane().add(passwordField);
		
		JButton btnPassword = new JButton("Enter");
		btnPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = String.valueOf(passwordField.getPassword());
				container.sendJoinRoom(id, roomName, password);
				dispose();
				
			}
		});
		btnPassword.setBounds(215, 71, 89, 23);
		getContentPane().add(btnPassword);
		setVisible(true);
	}
}

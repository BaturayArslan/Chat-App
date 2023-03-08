package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ReconnectionDialog extends JDialog {
	private Login container;
	
	public ReconnectionDialog(String message, final Login container) {
		this.container = container;
		setResizable(false);
		setTitle("Connection Error");
		setBounds(100, 100, 414, 144);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel lblError = new JLabel(message);
		lblError.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblError.setBounds(37, 11, 339, 23);
		getContentPane().add(lblError);
		
		JButton btnReconnect = new JButton("Reconnect");
		btnReconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				container.getWindowManager().reconnect();
				dispose();
			}
		});
		btnReconnect.setBounds(74, 54, 89, 23);
		getContentPane().add(btnReconnect);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				container.dispose();
			}
		});
		btnExit.setBounds(222, 54, 89, 23);
		getContentPane().add(btnExit);
		
		setVisible(true);
	}
}

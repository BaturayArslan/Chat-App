package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Font;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreateRoomDialog extends JDialog {
	private JTextField textRoomName;
	private JTextField textMaxUser;
	
	private static final int DEFAULT_ROOM_SIZE = 20;

	public CreateRoomDialog(final Room container) {
		setTitle("Create Room");
		setResizable(false);
		setBounds(100, 100, 379, 418);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		{
			JLabel lblRoomName = new JLabel("Room Name");
			lblRoomName.setFont(new Font("Tahoma", Font.PLAIN, 16));
			lblRoomName.setBounds(71, 52, 108, 29);
			getContentPane().add(lblRoomName);
		}
		
		textRoomName = new JTextField();
		textRoomName.setBounds(71, 84, 200, 20);
		getContentPane().add(textRoomName);
		textRoomName.setColumns(10);
		
		JLabel lblMaxUser = new JLabel("Max User");
		lblMaxUser.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblMaxUser.setBounds(71, 115, 108, 29);
		getContentPane().add(lblMaxUser);
		
		textMaxUser = new JTextField();
		textMaxUser.setBounds(71, 155, 200, 20);
		getContentPane().add(textMaxUser);
		textMaxUser.setColumns(10);
		
		
		final JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblPassword.setBounds(71, 225, 200, 27);
		lblPassword.setVisible(false);
		getContentPane().add(lblPassword);
		
		final JTextField textPassword = new JPasswordField();
		textPassword.setBounds(71, 263, 194, 20);
		getContentPane().add(textPassword);
		textPassword.setVisible(false);
		textPassword.setColumns(10);
		
		final JCheckBox chckbxPrivate = new JCheckBox("Private");
		chckbxPrivate.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {					
					lblPassword.setVisible(true);
					textPassword.setVisible(true);
				}else if (e.getStateChange() == ItemEvent.DESELECTED) {
					lblPassword.setVisible(false);
					textPassword.setVisible(false);
					textPassword.setText("");
				}
				
			}
		});
		chckbxPrivate.setBounds(68, 195, 97, 23);
		getContentPane().add(chckbxPrivate);
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int maxUser;
				String name = textRoomName.getText();
				try {
					maxUser = Integer.parseInt(textMaxUser.getText());					
				} catch (Exception e2) {
					maxUser = DEFAULT_ROOM_SIZE;
				}
				boolean isPublic = !chckbxPrivate.isSelected();
				String password = isPublic ? "": textPassword.getText();
				container.sendCreateRoom(name, maxUser, isPublic, password);
				dispose();
			}
		});
		btnCreate.setBounds(117, 317, 89, 23);
		getContentPane().add(btnCreate);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		CreateRoomDialog test = new CreateRoomDialog(null);
	}
}

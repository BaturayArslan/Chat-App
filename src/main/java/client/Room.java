package client;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import message.Message;
import message.MessageType;

public class Room extends JFrame {

	private JPanel contentPane;
	private JTable dataTable;
	private JButton btnJoin;
	private JButton btnCreate;

	private String username;
	private Connection connection;
	private WindowManager windowManager;
	private boolean active;
	
	private Thread receiveThread;
	
	
	public Room(String username, Connection connection, WindowManager manager) {
		this.username = username;
		this.connection = connection;
		this.windowManager = manager;
		
		if(connection != null) {
			active = true;
			startThread();
		}else {
			active = false;
		}
		
		createWindow();
		sendGetRooms();
	}
	
	@SuppressWarnings("serial")
	private void createWindow() {

		setTitle("Rooms");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600,400);
		setLocationRelativeTo(null);
		
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{500};
		gbl_contentPane.rowHeights = new int[]{275,25};
		gbl_contentPane.columnWeights = new double[]{1.0};
		gbl_contentPane.rowWeights = new double[]{1.0,1.0};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		dataTable = new JTable();
		dataTable.setShowVerticalLines(false);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataTable.setFocusable(false);
		dataTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"id","Room Name", "Users", "Public"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class,String.class, String.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false,false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		
		dataTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		dataTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		
		// Add custom rows
		//DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
		//model.addRow(new Object[] {"1","Column1", "Column2",true});
		//model.addRow(new Object[] {"2","Column4", "Column5",false});
		
		//Center cell input
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
		dataTable.setDefaultRenderer(String.class, centerRenderer);
		
		JTableHeader header = dataTable.getTableHeader();
		header.setDefaultRenderer(centerRenderer);
		
		scrollPane.setViewportView(dataTable);
		
		JPanel btnPanel = new JPanel();
		GridBagConstraints gbc_btnPanel = new GridBagConstraints();
		gbc_btnPanel.fill = GridBagConstraints.BOTH;
		gbc_btnPanel.gridx = 0;
		gbc_btnPanel.gridy = 1;
		contentPane.add(btnPanel, gbc_btnPanel);
		FlowLayout fl_btnPanel = new FlowLayout(FlowLayout.RIGHT, 5, 5);
		btnPanel.setLayout(fl_btnPanel);
		
		btnCreate = new JButton("Create Room");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateRoomDialog dialog = new CreateRoomDialog(Room.this);
			}
		});
		btnPanel.add(btnCreate);
		
		btnJoin = new JButton("     Join     ");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
				int row = dataTable.getSelectedRow();
				if(row >= 0) {
					int id = Integer.parseInt((String) model.getValueAt(row, 0));
					String name = (String) model.getValueAt(row, 1);
					boolean isPublic = (boolean) model.getValueAt(row, 3);
					if(isPublic) {
						sendJoinRoom(id, name, "");
						
					}else {
						new PasswordDialog(Room.this,id ,name);
					}					
				}
			}
		});
		btnPanel.add(btnJoin);
		
		
		setVisible(true);
	
	}
	
	public void sendJoinRoom(int id , String roomName, String password) {
		String message = id  + "," + password;
		
		connection.send(new Message(9,message));
		
	}
	
	public void sendCreateRoom(String roomName, int maxUser, boolean isPublic, String password) {
		String message;
		message = roomName + "," + maxUser+ "," + isPublic + "," +password;		
		connection.send(new Message(10,message));
	}
	
	public void sendGetRooms() {
		connection.send(new Message(12,""));
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
						windowManager.activateLogin();
						ErrorDialog error = new ErrorDialog("Connection Interupt.");
						dispose();
						System.out.println("Disconnected");	
					}					
				}
			}
		};
		receiveThread.setName("Room Receive Thread");
		receiveThread.start();
	}
	
	public void process(Message message) {
		if (message.getMessageCode() == MessageType.JOIN_ROOM.getCode()) {
			// Join Room
			active = false;
			dispose();
			windowManager.activateChat(username);
			
		}else if (message.getMessageCode() == MessageType.CREATE_ROOM.getCode()) {
			// Create Room
			active = false;
			dispose();
			windowManager.activateChat(username);
			
		}else if(message.getMessageCode() == MessageType.ERROR.getCode()) {
			// Error occured
			new ErrorDialog(message.getMessage());
			
		}else if(message.getMessageCode() == MessageType.INFORM_LOBY.getCode()) {
			// Update room status --> "id,roomName,userCount,isPublic"
			String[] roomInfo = message.getMessage().split(",");
			
			String roomName = roomInfo[1];
			String id = roomInfo[0];
			String userCount = roomInfo[2];
			boolean isPublic = Boolean.valueOf(roomInfo[3]);
			
			updateRoom(id, roomName, userCount, isPublic);
			
		}else if(message.getMessageCode() == MessageType.GET_ROOMS.getCode()) {
			// Get all room status --> "id,roomName,userCount,isPublic,id,roomName,userCount,isPublic...."
			initRooms(message.getMessage());
			
		}else {
			System.out.println("Unknown message type: " + message.getMessageCode() + ":" +message.getMessage());
		}
	}
	
	private void updateRoom(String id, String roomName, String userCount, boolean isPublic) {
		DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
		
		for(int i = 0; i < model.getRowCount(); i++) {
			String rowId = (String) model.getValueAt(i, 0);
			if(rowId.equals(id)) {
				model.setValueAt(id, i, 0);
				model.setValueAt(roomName, i, 1);
				model.setValueAt(userCount, i, 2);
				model.setValueAt(isPublic, i, 3);
				return;
			}
		}
		// There is no room to be update than insert new room (room creation)
		model.addRow(new Object[] {id, roomName, userCount, isPublic});

	}
	
	private void initRooms(String message) {
		 // message --> id,roomName,userCount,isPublic,id,roomName,userCount,isPublic....
		
		String[] rowInfo = message.split(",");
		int rowCount = rowInfo.length / 4;
		DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
		
		for(int i = 0; i < rowCount; i++) {
			model.addRow(new Object[] {rowInfo[i*4 + 0],rowInfo[i*4 + 1], rowInfo[i*4 + 2],Boolean.valueOf(rowInfo[i*4 + 3])});
		}
	}
	
}

package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import message.Message;
import message.MessageType;


public class Client {
	private String username;
	private boolean isLogged;
	private InetAddress ip;
	private int port;
	private int attempt;
	private Socket socket;
	private Server server;
	private Room room;
	private UUID ID; 
	
	private Queue<Byte> remainingBytes;
	private Thread receiveThread;
	
	public final static int PACKETSIZE = 512;
	
	
	public Client(Socket socket, Server server, UUID id) {
		this.socket = socket;
		this.port = socket.getPort();
		this.ip = socket.getInetAddress();
		this.ID = id;
		this.server = server;
		isLogged = false;
		remainingBytes = new LinkedList<Byte>();
		
		receive();
	}
	
	private void receive() {
		receiveThread = new Thread("receive Thread") {
			public void run() {
				try {
					while(true) {
						Message message = getMessage();
						process(message);

					}					
				} catch (Exception e) {
					// Mainly IOException and InterrruptException
					disconnect();
				}
				
			}
		};
		receiveThread.start();
	}
	
	private void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(isLogged) {
				room.leave(username);
				informLoby(room);
			}else {
				server.disconnect(ID);				
			}
			System.out.println(username + " disconnected from " + ip.getHostAddress() + ":" + port);
		}
	}
	
	private Message getMessage() throws IOException, InterruptedException {
		ArrayList<Byte> message = new ArrayList<Byte>();
		
		// Add remaining byte after byte that indicate end of message to message
		while(!remainingBytes.isEmpty()) {
			message.add(Byte.valueOf(remainingBytes.remove()));
		}
		
		
		byte[] buffer = new byte[PACKETSIZE];
		int count = 0;
		while(count != -1 ) {
			InputStream is = socket.getInputStream();
			count = is.read(buffer, 0, buffer.length);
			if(buildMessage(message, buffer, count)) {
				// message ready to go
				byte[] arr = new byte[message.size()];
				int j = 0;
				for(Byte obj : message) {
					arr[j++] = obj.byteValue();
				}
				return new Message((int)message.get(0), new String(arr, 1, arr.length - 1));
			}			
			Thread.sleep(100);
		}
		
		// connection closed
		throw new IOException();
		
	}
	
	private boolean buildMessage(ArrayList<Byte> message, byte[] buffer, int limit) {
		for(int i = 0; i < limit; i++) {
			if(buffer[i] == MessageType.MESSAGEEND.getCode()) {
				// We reach end of message. Record remaining bits to remainingBytes.
				for(int j = i + 1 ; j < limit; j++) {
					remainingBytes.add(buffer[j]);
				}
				return true;
			}
			message.add(buffer[i]);
		}
		return false;
	}
	
	private void process(Message messageObj) {
		String message = messageObj.getMessage();
		if(messageObj.getMessageCode() == MessageType.CONNECTION.getCode()) {
			// Client want to connect connect client and inform client --> "username,password"
			String[] loginInfo = message.split(",");
			login(loginInfo[0], loginInfo[1]);
			
		}else if (messageObj.getMessageCode() == MessageType.TEXTMESSAGE.getCode()) {
			// Client want to send message send messages all other client -> "/m/<message>"
			room.sendAll(messageObj);
			
		}else if (messageObj.getMessageCode() == MessageType.REGISTER.getCode()) {
			// Client want to register --> "username,password"
			String[] registerInfo = message.split(",");
			register(registerInfo[0], registerInfo[1]);
			
		}else if (messageObj.getMessageCode() == MessageType.JOIN_ROOM.getCode()) {
			// Client want to join room --> "id,password"
			String[] roomInfo = message.split(",");
			if(roomInfo.length == 2) {
				joinRoom(Integer.parseInt(roomInfo[0]), roomInfo[1]);				
			}else {
				joinRoom(Integer.parseInt(roomInfo[0]), "");
			}
			
		}else if(messageObj.getMessageCode() == MessageType.CREATE_ROOM.getCode()) {
			// Client want to create room --> "name,maxcount,isPublic,password"
			String[] roomInfo = message.split(",");
			
			String name = roomInfo[0];
			int maxCount = Integer.parseInt(roomInfo[1]);
			boolean isPublic = Boolean.valueOf(roomInfo[2]);
			String password = "";
			if(!isPublic) {
				password = roomInfo[3];
			}
			
			createRoom(name, maxCount, isPublic, password);
			
		}else if(messageObj.getMessageCode() == MessageType.GET_ROOMS.getCode()) {
			// Client want to get all room info --> "114" there is only prefix(11) and postfix(4) byte as a message
			allRoomInfo();
			
		}else if(messageObj.getMessageCode() == MessageType.GET_USERS.getCode()) {
			// Client want to get all room info --> "134" there is only prefix(13) and postfix(4) byte as a message
			getUsers();
			
		}else if(messageObj.getMessageCode() == MessageType.LEAVE_ROOM.getCode()) {
			// Client want to inform room --> "16username4" 
			leaveRoom(username);
			
		}else {
			System.out.println(message);
			
		}
	}
	
	private void login(String username, String password) {
		try {
			Connection db = server.getDb();
			// TODO:: fix sql injection later
			String query = "SELECT username, password FROM nodeapp.chat_users WHERE username='" + username + "'";
			Statement statement = db.createStatement();
			ResultSet result = statement.executeQuery(query);
			if(result.next()) {
				String passwordData = result.getString("password");
				// TODO :: encrypt password later
				String usernameData = result.getString("username");
				if(password.equals(passwordData)) {
					//User found and correct credential.
					if(server.getRoomManager().InRooms(username)) {
						// User already logged in 
						send(new Message(5, "You Already Logged In"));
						return;
					}
					this.isLogged = true;
					this.username = usernameData;
					this.server.removeClient(this);
					System.out.println(username + " logged in from " + ip.getHostAddress()  + ":" + port);
					room = this.server.getRoomManager().enterLoby(this);
					send(new Message(6,username));
				}else {
					// User found but invalid credential.
					send(new Message(5, "Invalid password.Please try again."));
				}
			}else {
				// User didn't found
				send(new Message(5, "User couldn't found with " + username + " username"));
			}
		} catch (SQLException e) {
			// Connection with database couldn't established or sql error
			e.printStackTrace();
			send(new Message(5, "Server Error.Please try later."));
		} 
	}
	
	private void register(String username, String password) {
		try {
			Connection db = server.getDb();
			// TODO:: fix sql injection later
			String query = "INSERT INTO nodeapp.chat_users (username, password) VALUES ('" + username + "','" + password + "')";
			Statement statement = db.createStatement();
			statement.execute(query);
			send(new Message(7, "Succesfully Registered. Please login in."));
		} catch (SQLIntegrityConstraintViolationException n) {
			// Duplicate username
			send(new Message(5, "Username has been taken."));
		}catch (SQLException e) {
			// Connection with database couldn't established or sql error
			e.printStackTrace();
			send(new Message(5, "Server Error.Please try later."));
		}
	}
	
	private void joinRoom(int id, String password) {
		if(isLogged) {
			try {
				Room result = server.getRoomManager().joinRoom(id, password, this);
				if(result != null) {
					room.leave(this.username);
					room = result;
					send(new Message(9,username));
					informLoby(result);
					sendUserJoin(result);
				}else {
					send(new Message(5,"Room is full or password incorrect"));
				}
				return;				
			} catch (Exception e) {
				send(new Message(5,"System Error when Joining Room."));
			}
			return;
		}
		send(new Message(5,"Invalid Room Join Request."));
	}
	
	private void leaveRoom(String username) {
		if(isLogged) {
			try {
				room.leave(username);
				sendUserLeave(room);
				room = server.getRoomManager().enterLoby(this);
				send(new Message(16,username));
			} catch (Exception e) {
				send(new Message(5,"System Error when Leaving Room."));
			}
			return;
		}
		send(new Message(5,"Invalid Room Leave Request."));
	}
	
	private void createRoom(String name, int maxCount, boolean isPublic, String password) {
		if(isLogged) {
			try {
				Room newRoom = server.getRoomManager().createRoom(name, maxCount, isPublic, password);
				if(newRoom.addUser(this)) {
					room.leave(this.username);
					room = newRoom;
					String messageStr = name + "," + maxCount + "," + isPublic;
					Message message = new Message(10, messageStr);
					send(message);
					informLoby(newRoom);
				}else {
					// Some thing went wrong maybe because user input. Rollback changes
					if(newRoom != null) {
						server.getRoomManager().deleteRoom(newRoom.getID());						
					}
					room = server.getRoomManager().getLoby();
					send(new Message(5, "User couldn't join room.Please Again."));
				}
				
			} catch (Exception e) {
				send(new Message(5,"System Error when Room Creation."));
			}
			return;
		}
		send(new Message(5,"Invalid Room Creation Request."));
	}
	
	public void send(final Message message) {
		Thread send = new Thread(){
			public void run() {
				List<byte[]> list = dividePackets(message, PACKETSIZE);
				try {
					OutputStream os = socket.getOutputStream();
					for(int i = 0; i < list.size(); i++) {
						os.write(list.get(i));
						os.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
					disconnect();
				}
			}
		};
		send.start();
	}
	
	 private List<byte[]> dividePackets(Message message, int size) {
		 String messageString = message.getMessage();
		 byte[] messageBytes = addFix(messageString.getBytes(), message.getMessageCode());
		 List<byte[]> list = new ArrayList<byte[]>();
		 
		 if(messageBytes.length <= PACKETSIZE) {
			 list.add(messageBytes);
			 return list;
		 }
		 
		 int index = 0;
		 int length = messageBytes.length;
		 
		 while(index < messageBytes.length) {
			 if(length > size) {
				 byte[] tmp = new byte[size];
				 for(int j = 0; j < size; j++) {
					tmp[j] = messageBytes[index + j]; 
				 }				
				 list.add(tmp);
				 index += size;
				 length -= size;
			 }else {
				 byte[] tmp = new byte[length];
				 for(int j = 0; j < length; j++) {
					tmp[j] = messageBytes[index + j]; 
				 }
				 list.add(tmp);
				 index += length;
				 length -= length;
			 }
		 }
		 
		 return list;
	 }
	 
	 private byte[] addFix(byte[] message, int prefix) {
		 // add prefix add postfix byte codes
		 byte[] result = new byte[message.length + 2];
		 result[0] = (byte) prefix;
		 
		 for(int i = 1; i <= message.length ; i++) {
			 result[i] = message[i - 1];
		 }
		 
		 result[result.length - 1] = (byte) MessageType.MESSAGEEND.getCode();
		 return result;
	 }
	
	private void informLoby(Room room) {
		int id = room.getID();
		String name = room.getName();
		String userCount = room.getSize() + "/" + room.getMaxUser();
		boolean isPublic = room.isPublic();
		String messageStr = id + "," + name + "," + userCount + "," + isPublic;
		server.getRoomManager().sendLoby(new Message(11, messageStr));
	}
	
	private void sendUserJoin(Room room) {
		room.sendAll(new Message(14,username));
	}
	
	private void sendUserLeave(Room room) {
		room.sendAll(new Message(15,username));
	}
	
	private void allRoomInfo() {
		Room[] roomList = server.getRoomManager().getAllRoom();
		StringBuilder messageBuilder = new StringBuilder();
		for(int i = 0; i < roomList.length; i++) {
			Room tmp = roomList[i];
			messageBuilder.append(tmp.getID() + "," + tmp.getName() + "," + tmp.getSize() + "/" + tmp.getMaxUser() + "," + tmp.isPublic() + ",");
		}
		
		send(new Message(12,messageBuilder.toString()));
	}
	

	
	private void getUsers() {
		Client[] userList = room.getUsers();
		StringBuilder messageBuilder = new StringBuilder();
		
		for(int i = 0; i < userList.length; i++) {
			Client tmp = userList[i];
			messageBuilder.append(tmp.getName() + ",");
		}
		
		send(new Message(13,messageBuilder.toString()));		
	}
	public String toString() {
		return "{ " + username + " from "+ip.getHostAddress() + ":"+ port + " and id is " + ID.toString() + " }";
	}
	
	public int getPort() {
		return this.port;
	}
	
	public InetAddress getAddress() {
		return this.ip;
	}
	
	public String getName() {
		return this.username;
	}
	
	public int getAttempt() {
		return attempt;
	}
	
	public void incAttempt() {
		attempt += 1;
	}
	
	public void resetAttempt() {
		attempt = 0;
	}
	
	public UUID getId() {
		return  ID;
	}
	
}

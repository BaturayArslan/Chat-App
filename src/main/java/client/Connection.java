package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import message.Message;
import message.MessageType;

public class Connection {

	private InetAddress ip;
	private int port;
	private Socket socket;
	private boolean active;
	
	private Queue<Byte> remainingBytes;
	
	public final static int PACKETSIZE = 512;
	
	public Connection(String address, int port) throws IOException {
		this.ip = InetAddress.getByName(address);
		this.port = port;
		this.active = true;
		this.socket = new Socket(ip, port);
		remainingBytes = new LinkedList<Byte>();


			

		
	}
	
	public synchronized Message recieve() throws IOException, InterruptedException{
	
		ArrayList<Byte> message = new ArrayList<Byte>();
		
		// Add remaining byte after byte that indicate end of message  to message
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
		
		//Connection closed
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
	
	
	
	public void send(Message message) {
		final List<byte[]> list = dividePackets(message, PACKETSIZE);
		Thread sendThread = new Thread("sendThread") {
			public void run() {
				try {
					OutputStream os = socket.getOutputStream();
					for(int i = 0; i < list.size(); i++) {
						os.write(list.get(i));
						os.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
					active = false;
				}
			}
		};
		sendThread.start();
	}


	
	public int getPort() {
		return port;
	}
	
	public String getAddress() {
		return ip.getHostAddress();
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	 public void setActive(boolean value) {
		 this.active = value;
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
	 
	 public void close() {
		 try {
			active = false;
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	

	

	
	
	
}

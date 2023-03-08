package message;



public class Message {
	private String message;
	private MessageType type;
	
	
	public Message(int code,String message) {
		byte tmp = (byte) code;
		if(tmp == MessageType.TEXTMESSAGE.getCode()) {
			this.type = MessageType.TEXTMESSAGE;
			
		}else if (tmp == MessageType.CONNECTION.getCode()) {
			this.type = MessageType.CONNECTION;
			
		}else if (tmp == MessageType.DISCONNECTION.getCode() ) {
			this.type = MessageType.DISCONNECTION;
			
		}else if (tmp == MessageType.ERROR.getCode()) {
			this.type = MessageType.ERROR;
			
		}else if (tmp == MessageType.LOGIN_SUCCESS.getCode()) {
			this.type = MessageType.LOGIN_SUCCESS;
			
		}else if (tmp == MessageType.REGISTER_SUCCESS.getCode()) {
			this.type = MessageType.REGISTER_SUCCESS;
			
		}else if (tmp == MessageType.REGISTER.getCode()) {
			this.type = MessageType.REGISTER;
			
		}else if (tmp == MessageType.JOIN_ROOM.getCode()) {
			this.type = MessageType.JOIN_ROOM;
			
		}else if (tmp == MessageType.CREATE_ROOM.getCode()) {
			this.type = MessageType.CREATE_ROOM;
			
		}else if (tmp == MessageType.INFORM_LOBY.getCode()) {
			this.type = MessageType.INFORM_LOBY;
			
		}else if (tmp == MessageType.GET_ROOMS.getCode()) {
			this.type = MessageType.GET_ROOMS;
			
		}else if (tmp == MessageType.USER_JOİN.getCode()) {
			this.type = MessageType.USER_JOİN;
			
		}else if (tmp == MessageType.USER_LEAVE.getCode()) {
			this.type = MessageType.USER_LEAVE;
			
		}else if (tmp == MessageType.LEAVE_ROOM.getCode()) {
			this.type = MessageType.LEAVE_ROOM;
			
		}else if (tmp == MessageType.GET_USERS.getCode()) {
			this.type = MessageType.GET_USERS;
			
		}
		
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public int getMessageCode() {
		return type.getCode();
	}
	
}

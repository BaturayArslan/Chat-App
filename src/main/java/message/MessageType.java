package message;

public enum MessageType {
	TEXTMESSAGE (1),
	CONNECTION (2),
	DISCONNECTION (3),
	MESSAGEEND (4),
	ERROR (5),
	LOGIN_SUCCESS (6),
	REGISTER_SUCCESS (7),
	REGISTER (8),
	JOIN_ROOM (9),
	CREATE_ROOM (10),
	INFORM_LOBY (11),
	GET_ROOMS (12),
	GET_USERS (13),
	USER_JOİN (14),
	USER_LEAVE (15),
	LEAVE_ROOM (16);
	
	private final int code;
	
	private MessageType(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}


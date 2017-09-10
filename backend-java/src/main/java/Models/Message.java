import java.util.Map;
import java.sql.Timestamp;

public abstract class Message {
	public enum MessageType {
		TEXT,
		IMAGE,
		VIDEO
	}

	private int id;
	private int senderId;
	private int recipientId;
	private MessageType type;
	private String content;
	private String creationTime;

	public Message() {}

	public Message(Map<String, Object> map) {
		this.id = (int)map.get("id");
		this.senderId = (int)map.get("senderId");
		this.recipientId = (int)map.get("recipientId");
		this.type = MessageType.valueOf((String)map.get("type"));
		this.content = (String)map.get("content");
		this.creationTime = ((Timestamp)map.get("creationTime")).toString();
	}

	public int getId() {
		return id;
	}

	public int getSenderId() {
		return senderId;
	}

	public int getRecipientId() {
		return recipientId;
	}

	public MessageType getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public String getCreationTime() {
		return creationTime;
	}
}
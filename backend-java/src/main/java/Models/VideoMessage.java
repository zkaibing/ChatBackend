package models;

import java.util.Map;
import models.Message;

public class VideoMessage extends Message {
	private int length;
	private String source;

	public VideoMessage() {
		super();
	}

	public VideoMessage(Map<String, Object> map) {
		super(map);
		this.length = (int)map.get("length");
		this.source = (String)map.get("source");
	}

	public int getLength() {
		return length;
	}

	public String getSource() {
		return source;
	}
}
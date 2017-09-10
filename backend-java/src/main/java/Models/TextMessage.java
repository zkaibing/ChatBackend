package models;

import java.util.Map;
import models.Message;

public class TextMessage extends Message {
	public TextMessage() {
		super();
	}
	
	public TextMessage(Map<String, Object> map) {
		super(map);
	}
}
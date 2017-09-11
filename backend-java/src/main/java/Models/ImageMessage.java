package models;

import java.util.Map;
import models.Message;

public class ImageMessage extends Message {
	private int width;
	private int height;

	public ImageMessage() {
		super();
	}

	public ImageMessage(Map<String, Object> map) {
		super(map);
		this.width = (int)map.get("width");
		this.height = (int)map.get("height");
	}

	@Override
	public boolean validate() {
		return super.validate() && width > 0 && height > 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
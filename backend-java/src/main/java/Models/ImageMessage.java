import java.util.Map;

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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
package entityRaw;


import java.nio.ByteBuffer;

public class RawImage {
	private int width, height;
	private ByteBuffer image;
	
	public RawImage (ByteBuffer image, int width, int height) {
		this.image = image;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public ByteBuffer getImage() {
		return this.image;
	}
}

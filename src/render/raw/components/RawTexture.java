package render.raw.components;


import java.nio.ByteBuffer;

/**
 * Represents raw texture data
 *
 * The image data is stored in a ByteBuffer, which is compatible
 * with OpenGL texture operations 
 *
 * This class does not perform any GPU operations and is intended
 * as an intermediate representation of texture data.
 */

public class RawTexture {
	private int width, height;
	private ByteBuffer image;
	
	public RawTexture (ByteBuffer image, int width, int height) {
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

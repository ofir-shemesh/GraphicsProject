package render.components;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import render.raw.components.RawTexture;


public class Texture {
	private int width, height;
	private int texID;
	private final boolean cubeMap;
	
	private static RawTexture getImageData(String path, boolean cubeMap) {
		IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        if (!cubeMap) {
        	STBImage.stbi_set_flip_vertically_on_load(true);
        } else {
        	STBImage.stbi_set_flip_vertically_on_load(false);
        	
        }
        	
        ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
        }
        
        if (cubeMap) {
        	int numChannels = 4; // if you requested RGBA
        	int stride = width.get(0) * numChannels;

        	for (int y = 0; y < height.get(0); y++) {
        	    int rowStart = y * stride;
        	    for (int x = 0; x < width.get(0) / 2; x++) {
        	        int left = rowStart + x * numChannels;
        	        int right = rowStart + (width.get(0) - 1 - x) * numChannels;

        	        for (int c = 0; c < numChannels; c++) {
        	            byte tmp = image.get(left + c);
        	            image.put(left + c, image.get(right + c));
        	            image.put(right + c, tmp);
        	        }
        	    }
        	}
        }
        
        return new RawTexture(image, width.get(0), height.get(0));
	}
	
	private void setupTextureRegular(int texture, String path) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        RawTexture imageData = getImageData(path, false);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imageData.getWidth(), imageData.getHeight(),
                     0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.getImage());
        glGenerateMipmap(GL_TEXTURE_2D);
        
        STBImage.stbi_image_free(imageData.getImage());
	}
	
	private void setupTextureCubeMap(int texture, String path) {
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	
		String[] files = {
			    "right.png", "left.png", "top.png",
			    "bottom.png", "front.png", "back.png"
			};

			for (int i = 0; i < 6; i++) {
		        RawTexture imageData = getImageData(path + files[i], true);
				
			    ByteBuffer image = imageData.getImage(); // load with STBImage or similar
			    width = imageData.getWidth();
			    height = imageData.getHeight();
			    
			    glTexImage2D(
			        GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, width, height,
			        0, GL_RGBA,GL_UNSIGNED_BYTE, image);
			    
		        STBImage.stbi_image_free(imageData.getImage());

			}
	}
	
	public Texture(String path, boolean cubeMap) {
		this.cubeMap = cubeMap;
		
		texID = glGenTextures();
        int TEXTURE_TYPE = cubeMap ? GL_TEXTURE_CUBE_MAP : GL_TEXTURE_2D;
		glBindTexture(TEXTURE_TYPE, texID);
        
		if (cubeMap) {
			setupTextureCubeMap(texID, path);
		}else {
			setupTextureRegular(texID, path);			
		}
		
		glBindTexture(TEXTURE_TYPE, 0);
	}
	
	public boolean isCubeMap() {
		return this.cubeMap;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getTexID() {
		return this.texID;
	}
	
	public void clean() {
        glDeleteTextures(texID);
	}
}

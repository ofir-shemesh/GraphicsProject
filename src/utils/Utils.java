package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import render.raw.components.RawTexture;

public class Utils {
	
	public static RawTexture getImageData(String path) {
		IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason());
        }
    	STBImage.stbi_set_flip_vertically_on_load(false);

        return new RawTexture(image, width.get(0), height.get(0));
	}
	
	public static RawTexture getImageData(String path, boolean cubeMap) {
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

	public static IntBuffer createIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data).flip();
        return buffer;
	}
	
	public static FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();
		return buffer;
	}

	//File
	public static String readFromFile(String file) {
		StringBuilder source = new StringBuilder();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!=null) {
				source.append(line).append("\n");
			}
			
		}catch(IOException e) {
			System.err.println("Could not read file!");
			e.printStackTrace();
			System.exit(-1);
		} finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		return source.toString();
	}
}

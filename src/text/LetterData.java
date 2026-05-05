package text;

/**
 * This class contains relevant information about a letter in a specific font
 * for rendering
 * 
 */
public class LetterData {
	// which letter is it
	char value;
	// x, y are the top left coordinates in the atlas
	// width, height are dimensions in the atlas
	float x, y, width, height;
	//xoffset, yoffset describe by how much i need to shift the character relative to the cursor
	//xadvance describes by how much i need to shift the cursor after this letter.
	float xoffset, yoffset, xadvance;
	
	/**
	 * @param the line for that letter in the font's .fnt file
	 */
	public LetterData(String line) {
		String[] parts = line.split("\\s+");
		value = (char) Integer.parseInt(parts[1].substring("id=".length()));
		
		x = Float.parseFloat(parts[2].substring("x=".length()));
		y = Float.parseFloat(parts[3].substring("y=".length()));
		
		width = Float.parseFloat(parts[4].substring("width=".length()));
		height = Float.parseFloat(parts[5].substring("height=".length()));
		
		xoffset = Float.parseFloat(parts[6].substring("xoffset=".length()));
		yoffset = Float.parseFloat(parts[7].substring("yoffset=".length()));
		
		xadvance = Float.parseFloat(parts[8].substring("xadvance=".length()));
	}
	
	// --- Getters ---
	
	public float getXOffset() {
		return this.xoffset;
	}
	
	public float getYOffset() {
		return this.yoffset;
	}
	
	public float getAspectRatio() {
		return width / height;
	}
	
	public float getXAdvance() {
		return this.xadvance;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
}

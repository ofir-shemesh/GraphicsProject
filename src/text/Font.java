package text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Font {
	private Map<Character, LetterData> data; 
	
	//scaleW, scaleH are the atlas's dimensions
	private float scaleW, scaleH;
	
	private float lineHeight;
	
	/**
	 * Helper for constructor
	 * - initializes scaleW, scaleH, lineHeight
	 * @param parts - the line containing the information in the .fnt file
	 */
	private void initCommon(String[] parts) {
		// Vertical distance between lines
        lineHeight = Float.parseFloat(
                parts[1].substring("lineHeight=".length())
        );

        // Texture atlas width
        scaleW = Float.parseFloat(
                parts[3].substring("scaleW=".length())
        );

        // Texture atlas height
        scaleH = Float.parseFloat(
                parts[4].substring("scaleH=".length())
        );
	}

	/**
	 * Helper for constructor
	 * - adds a letter to data
	 * @param parts - the space separated parts of the line containing the information in the .fnt file
	 */

	private void addChar(String line, int ascii) {
		// Extract char
        
        char c = (char) ascii;

        // Parse line to extract and store LetterData
        LetterData letterData = new LetterData(line);
        data.put(c, letterData);
	}
	/**
	 * Constructor
	 * 
	 * - NOTE: a few assumptions are made about the .fnt file
	 * 		   it works for the file I have but i am not aware of all the specifications
	 *  	   of the format and therefore the constructor may need future changes
	 *  	   that makes it more dynamic.
	 * 
	 * @param font_folder
	 */
	public Font(String fontFolder) {
    data = new HashMap<>();

    // Read File
    try (BufferedReader reader = new BufferedReader(
            new FileReader(fontFolder + "/info.fnt"))) {

        String line;

        // Read file line by line
        while ((line = reader.readLine()) != null) {

            String[] parts = line.split("\\s+");

            // --- COMMON LINE ---
            // this line contains the scaleW, scaleH, lineHeight information
            if (parts[0].equals("common")) {
                initCommon(parts);
            }

            // --- CHARACTER LINE ---
            /*
             *  those lines contains the data for each character
             * used to initialize this.data
             */
            else if (parts[0].equals("char")) {
            	
            	int ascii = Integer.parseInt(parts[1].substring(
                		"id=".length())
                		);
            	
                addChar(line, ascii);
            }
        }

    } catch (IOException e) {
        System.err.println("Could not read font file: " + fontFolder + "/info.fnt");
        e.printStackTrace();
        System.exit(-1);
    }
}
		
	// --- Getters ---
	public float getScaleW() {
		return this.scaleW;
	}
	
	public float getScaleH() {
		return this.scaleH;
	}
	
	public float getLineHeight() {
		return this.lineHeight;
	}

	public LetterData getLetterData(char c) {
		return data.get(c);
	}
	
	/**
	 * 
	 * @param letter
	 * @return UV coordinates for the letter's texture in the atlas
	 *         to be used in the fragment shader
	 */
	public float[] getUV(char letter) {
		
		LetterData letterData = data.get(letter);

		float width = letterData.getWidth() / scaleW;
		float height = letterData.getHeight() / scaleH;
		
		float x = letterData.getX() / scaleW;
		float y = 1-letterData.getY() / scaleH-height;
		
		return new float[] {
				x, y,
				x + width, y,
				x, y + height,
				x + width, y + height
			};
		
	}
	
}

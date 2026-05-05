package inventory;

import org.joml.Vector2f;

import color.Color;
import render.scene.Renderable;
import text.TextStyle;
import text.VariableText;

public class Inventory {
	private static final int MAX_COINS = 10;
	private static int coins;
	
	private static VariableText text;
	
	public static void init() {
		TextStyle style = new TextStyle(0.6f, 0.2f, new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(0.0f, 0.0f, 0.0f, 1.0f));
		String fontName = "font";
		
		coins = MAX_COINS;
		
		int max_length = getStatusString().length();
		
		coins = 0;
		
		text = new VariableText(max_length, 0.1f, new Vector2f(-0.9f, 0.7f), fontName, style, getStatusString());

	}
	
	public static void addCoins(int addition) {
		coins = Math.clamp(coins+addition, 0, MAX_COINS);
		
		text.updateText(getStatusString());
	}
	
	public static String getStatusString() {
		return coins + " / " + MAX_COINS;
	}
	
	public static void render() {
		text.render();
	}
	
	public static void clean() {
		text.clean();
	}
}

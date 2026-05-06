package gui;

import org.joml.Vector2f;

import color.Color;
import inventory.Inventory;
import sky.Time;
import text.TextStyle;
import text.VariableText;

public class GUI {
	private static VariableText timeText;
	private static VariableText scoreText;
	
	public static void init() {
		TextStyle style = new TextStyle(0.6f, 0.2f, new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(0.0f, 0.0f, 0.0f, 1.0f));
		String fontName = "font";

		// --- Time ---
		int max_length = "hh : mm".length();
		timeText = new VariableText(max_length, 0.1f, new Vector2f(-0.9f, 0.6f), fontName, style, "67 : 76");
		
		// --- Score ---
		max_length = Inventory.MAX_COINS * 2 + " / ".length();
		scoreText = new VariableText(max_length, 0.1f, new Vector2f(-0.9f, 0.7f), fontName, style, "");

	}
	
	public static void render() {
		timeText.render();
		scoreText.render();
	}
	
	public static void tick() {
		updateTime();
		updateScore();
	}
	
	public static void updateTime() {
		/**
		 * @return textual representation of the time as hh:mm
		 */
	    float hours_f = Time.getTime() * 24;
	    int hours = (int) Math.floor(hours_f);
	    int minutes = (int) Math.floor(60 * (hours_f - hours));

	    // Format with leading zeros
	    String newText =  String.format("%02d:%02d", hours, minutes);
	    
		timeText.updateText(newText);
	}
	
	public static void updateScore() {
		String newText = "" + Inventory.getCoins() + " / " + Inventory.MAX_COINS;
		scoreText.updateText(newText);		
	}
	
	public static void clean() {
		timeText.clean();
		scoreText.clean();
	}
}

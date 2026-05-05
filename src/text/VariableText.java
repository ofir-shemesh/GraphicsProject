package text;

import java.util.Arrays;

import org.joml.Vector2f;

import render.components.ShaderProgram;
import render.components.Texture;
import render.factory.RawModelFactory;
import render.raw.components.RawModel;
import render.scene.Renderable;

public class VariableText {
	private final int maxLength;
	private final Font font;
	private float lineHeight;
	private Vector2f position;
	private TextStyle style;
	
	private final Renderable renderable;
	
	public VariableText(int maxLength, float lineHeight, Vector2f position, String fontName, TextStyle style, String initial_text) {
		this.style = style;
		this.maxLength = maxLength;
		this.lineHeight = lineHeight;
		this.position = position;
		
		font = new Font("res/fonts/" + fontName);

		RawModel model = RawModelFactory.createLineRawModel(
				new Vector2f(position),
				lineHeight, initial_text,
				font,
				maxLength-initial_text.length());
		
		ShaderProgram shaderProgram = new ShaderProgram("res/shaders/font/vert.vert", "res/shaders/font/frag.frag");
		setStyleShaders(shaderProgram);
		Texture texture = new Texture("res/fonts/" + fontName + "/atlas.png", false);
		Texture[] textures = {texture};
		
		renderable = new Renderable(model, shaderProgram, textures );
	}
	
	private void setStyleShaders(ShaderProgram program) {
		program.editUniform("color", style.getColor());
		program.editUniform("strokeColor", style.getStrokeColor());
		program.editUniform("thickness", style.getThickness());
		program.editUniform("strokeThickness", style.getStrokeThickness());		
	}
	
	public void updateText(String new_text) {
		int length = Math.min(maxLength, new_text.length());
		String trimmed_text = new_text.substring(0, length);
		
		renderable.updateFloatBufferData(RawModelFactory.createLineRawModel(new Vector2f(position), lineHeight,
				trimmed_text, font, maxLength-trimmed_text.length()));
	}
	
	public void renderGUI() {
		renderable.render(true);
	}
	
	public void clean() {
		renderable.clean();
	}
	
}
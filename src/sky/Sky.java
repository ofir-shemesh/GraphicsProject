package sky;

import color.Color;
import color.ColorGradient;
import entityData.ShaderProgram;
import utils.MyMath;

public class Sky {
	private ColorGradient gradient_top, gradient_bottom;
	private Runnable postColor;
	
	public Sky(ColorGradient gradient_top, ColorGradient gradient_bottom) {
		this.gradient_top = gradient_top;
		this.gradient_bottom = gradient_bottom;
		this.postColor = () -> {
			
		};
	}
	
	public void addPostColor(Runnable addition) {
		Runnable old = postColor;
		
		postColor = ()-> {
			old.run();
			addition.run();
		};
	}

	public void applyToShader(ShaderProgram program) {
		Color color_top = gradient_top.getColor(Time.getTime());
		Color color_bottom = gradient_bottom.getColor(Time.getTime());
		
		program.editUniform("color_top", color_top);
		program.editUniform("color_bottom", color_bottom);
		program.editUniform("starsIntensity", getStarsIntensity());

		postColor.run();
	}

	
	public Color getColorTop() {
		Color color_top = gradient_top.getColor(Time.getTime());
		return color_top;
	}
	
	public Color getColorBottom() {
		Color color_bottom = gradient_bottom.getColor(Time.getTime());
		return color_bottom;
	}
	
	private float getStarsIntensity() {
		float time24 = Time.getTime() * 24;
		
		// time in hours (0..24)
		    float intensity = 0.0f;

		    // Night start/end times
		    float nightStart = 18.0f; // 18:00
		    float fullNightStart = 20.0f; // 20:00
		    float fullNightEnd = 4.0f;    // 04:00
		    float nightEnd = 6.0f;        // 06:00

		    if (time24 >= nightStart && time24 <= 24.0) {
		        // Evening fade in
		        intensity = MyMath.smoothstep(nightStart, fullNightStart, time24);
		    } 
		    else if (time24 >= 0.0 && time24 <= fullNightEnd) {
		        // Late night fully on
		        intensity = 1.0f;
		    } 
		    else if (time24 >= fullNightEnd && time24 <= nightEnd) {
		        // Morning fade out
		        intensity = MyMath.smoothstep(fullNightEnd, nightEnd, time24); // flips from 1->0
		        intensity = 1.0f - intensity;
		    } 
		    else {
		        // Daytime
		        intensity = 0.0f;
		    }

		    return intensity;

	}
}
package utils;

import color.Color;

/**
 * this class contains extra math needed in the game, and not provided in the standard Math class
 */

public class MyMath {
	public final static float pi = (float) Math.PI;

	public static float smoothstep(float edge0, float edge1, float x) {
	    float t = (x - edge0) / (edge1 - edge0);
	    t = Math.max(0.0f, Math.min(1.0f, t));

	    return t * t * (3.0f - 2.0f * t);
	}

	//Interpolations
	public static float lerp(float val1, float val2, float t) {
		return val1*(1.0f-t)+val2*t;
	}
	
	public static Color lerp(Color c1, Color c2, float t) {
        return new Color(
                lerp(c1.getR(), c2.getR(), t),
                lerp(c1.getG(), c2.getG(), t),
                lerp(c1.getB(), c2.getB(), t),
                lerp(c1.getA(), c2.getA(), t)
        );
    }

	public static float clamp(float val, float min, float max) {
		return (float) Math.min(Math.max(val, min), max);
	}
}

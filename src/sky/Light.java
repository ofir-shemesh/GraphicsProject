package sky;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

/**
 * Represents an orbiting light source.
 * 
 * in initialization allows a custom axis of rotation and speed.
 * 
 * -static class
 */

public class Light {
	/*
	 * rotates around the north, in the plane containing west
	 */
	private static Vector3f north;
	private static Vector3f west;
	
	private static float angle;
	private static float speed;
	
	private static List<Runnable> onRotChangeListeners = new ArrayList<>();
	
	/*
	 * initializes light
	 * 
	 * west is being initializes to the orthogonal projection of west_input on north_input
	 */
	public static void init(Vector3f north_input, Vector3f west_input, float input_angle, float input_speed) {
		// north
		north = north_input.normalize();
		Vector3f nor_north = new Vector3f(north);
		
		// west
		float proj = nor_north.dot(west_input);
		west = west_input.sub(nor_north.mul(proj));
		west.normalize();
		
		//rotation variables
		angle = input_angle;
		speed = input_speed;
	}
	
	
	public static void addPostRotEdit(Runnable listener) {
		onRotChangeListeners.add(listener);
		
	}
	
	//Getters
	
	public static float getAngle() {
		return angle;
	}
	
	public static Vector3f getDirection() {
		return new Vector3f(west).rotateAxis(angle, north.x, north.y, north.z);
	}
	
	/**
	 * rotates the light source and runs the runnables
	 */

	public static void tick() {
		angle += speed;
		
		for (Runnable r : onRotChangeListeners) {
			r.run();
		}
	}
}

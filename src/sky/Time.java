package sky;

import java.util.ArrayList;
import java.util.List;

/**
 * class representing the "Game time"
 * 
 * - enables adding runnables after each tick
 * - the time is between [0,1]
 * - enables textual time representation as hh:mm
 */
public class Time {
	public static float SPEED = 0.0004f;
	private static float time = 0.0f;
	
	private final static List<Runnable> onTickListeners = new ArrayList<>();
	
	public static void addPostTick(Runnable listener) {
		onTickListeners.add(listener);
	}
	
	public static float getTime() {
		return time;
	}
	
	/**
	 * - advances time
	 * - runs the runnables
	 */
	public static void tick() {
		time += SPEED;
		time %= 1.0f;
		
		for (Runnable r : onTickListeners) {
	        r.run();
	    }
	}
}

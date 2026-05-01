package sky;

public class Time {
	public static float SPEED = 0.00015f;
	private static float time = 0.3f;
	
	public static String getTimeText() {
	    float hours_f = time * 24;
	    int hours = (int) Math.floor(hours_f);
	    int minutes = (int) Math.floor(60 * (hours_f - hours));

	    // Format with leading zeros
	    return String.format("%02d:%02d", hours, minutes);
	}
	
	public static float getTime() {
		return time;
	}
	
	public static void tick() {
		time += SPEED;
		time %= 1.0f;
	}
}

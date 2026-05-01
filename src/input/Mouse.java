package input;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import main.Window;
import utils.FloatQuadRunnable;
import entityRaw.RawImage;
import utils.Utils;

public class Mouse {
	private static List<FloatQuadRunnable> movementActions = new ArrayList<>();
	private static float[] x, y;
	private static boolean on = false;
	
	private static int cursor_width = 32, cursor_height = 32;
	private static GLFWImage cursor_image;
	private static long cursor;
	public static boolean getOn() {
		return on;
	}
	
	public static void flip() {
		on = !on;
		System.out.println("on=" + on);
		GLFW.glfwSetInputMode(Window.getWindow(), GLFW.GLFW_CURSOR,
				on ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_DISABLED);
		
		if (on)
	    	GLFW.glfwSetCursor(Window.getWindow(), cursor);

	}
		
    public static void init() {
    	Vector2f cursorPosition = getCursorPosition();
    	
    	x = new float[] {cursorPosition.x};
    	y = new float[] {cursorPosition.y};
    	
    	GLFW.glfwSetCursorPosCallback(Window.getWindow(), (win, xpos, ypos) -> {
    		runMovementActions((float) xpos, (float) ypos);
    	});
    	
    	cursor_image = GLFWImage.malloc();
    	RawImage image_data = Utils.getImageData("res/textures/cursor/cursor.png"); // your own image loader
    	cursor_image.set(image_data.getWidth(), image_data.getHeight(), image_data.getImage());

    	int hotX = 0;
    	int hotY = 0;
    	
    	// Create the cursor
    	cursor = GLFW.glfwCreateCursor(cursor_image, hotX, hotY); // hotX/hotY = hotspot offset

    	// Set it

    	// Clean up when done
    	
    }
    
    public static void clean() {
    	cursor_image.free();
    	GLFW.glfwDestroyCursor(cursor);

    }
    
    public static Vector2f getCursorPosition() {
    	DoubleBuffer xbuffer = BufferUtils.createDoubleBuffer(1);
    	DoubleBuffer ybuffer = BufferUtils.createDoubleBuffer(1);
    	
    	GLFW.glfwGetCursorPos(Window.getWindow(), xbuffer, ybuffer);
    	
    	float cursor_x = (float) xbuffer.get(0);
    	float cursor_y = (float) ybuffer.get(0);
    	
    	return new Vector2f(cursor_x, cursor_y);
    }
    
    public static void onMovement(FloatQuadRunnable action) {
    	movementActions.add(action);
    }

    private static void runMovementActions(float xpos, float ypos) {
        if (movementActions != null) {
            for (FloatQuadRunnable r : movementActions) {
                r.run(x[0], y[0], xpos, ypos);
            }
        }
        
        x[0] = xpos;
        y[0] = ypos;
    }
}

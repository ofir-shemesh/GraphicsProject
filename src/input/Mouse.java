package input;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import main.Window;

/**
 * a static class representing the mouse.
 * 
 * - enables adding functionality to mouse movement.
 * - enables getting mouse position 
 * 
 * needs to be explicitly initialized
 */
public class Mouse {
	private static List<FloatQuadRunnable> movementRunnable = new ArrayList<>();
	private static float[] x, y;
		
	/**
	 * must be called before use
	 * 
	 * initializes x, y
	 * hides the mouse
	 * 
	 */
    public static void init() {
    	//hide the cursor
    	GLFW.glfwSetInputMode(Window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		
    	// initialize x, y
    	Vector2f cursorPosition = getCursorPosition();
    	
    	x = new float[] {cursorPosition.x};
    	y = new float[] {cursorPosition.y};
    	
    	// make sure that when the mouse moves runMovementActions() will run
    	GLFW.glfwSetCursorPosCallback(Window.getWindow(), (win, xpos, ypos) -> {
    		runMovementRunnable((float) xpos, (float) ypos);
    	});
    }
    
    /**
     * @return cursor's position
     */
    public static Vector2f getCursorPosition() {
    	DoubleBuffer xbuffer = BufferUtils.createDoubleBuffer(1);
    	DoubleBuffer ybuffer = BufferUtils.createDoubleBuffer(1);
    	
    	GLFW.glfwGetCursorPos(Window.getWindow(), xbuffer, ybuffer);
    	
    	float cursor_x = (float) xbuffer.get(0);
    	float cursor_y = (float) ybuffer.get(0);
    	
    	return new Vector2f(cursor_x, cursor_y);
    }
    
    /**
     * adds movement action
     * @param a runnable accepting 4 floats (prev_x, prev_y, current_x, current_y) to run when moving the mouse
     */
    public static void addMovementRunnable(FloatQuadRunnable action) {
    	movementRunnable.add(action);
    }
    
    /**
     * runs all the runnables in movementActions
     * @param xpos - the current mouse x coordinate
     * @param ypos - the current mouse y coordinate
     */
    private static void runMovementRunnable(float xpos, float ypos) {
        if (movementRunnable != null) {
            for (FloatQuadRunnable r : movementRunnable) {
                r.run(x[0], y[0], xpos, ypos);
            }
        }
        
        x[0] = xpos;
        y[0] = ypos;
    }
}

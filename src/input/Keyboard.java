package input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Window;

import static org.lwjgl.glfw.GLFW.*;

/**
 * a static class representing the mouse.
 * 
 * - enables adding functionality to key events
 * - enables adding functionality to hold events
 * 
 * needs to be explicitly initialized
 */
public class Keyboard {
	
	/**
	 * Hash Maps for Runnables to run on press and hold
	 * the Integer key is the GLFW representation of the key
	 */
    private static Map<Integer, List<Runnable>> keyActions = new HashMap<>();
    private static Map<Integer, List<Runnable>> keyHoldActions = new HashMap<>();
    
    private static void runActions(int key) {
        List<Runnable> actions = keyActions.get(key);
        if (actions != null) {
            for (Runnable r : actions) {
                r.run();
            }
        }
    }
    
    /**
	 * initialize press runnables
	 */

    public static void init() {
    	glfwSetKeyCallback(Window.getWindow(), (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                runActions(key);
            }
        });
    }
    
    /**
     * runs the relevant hold runnables
     * 
     * - should be called every tick
     */
    public static void tick() {
    	for (Map.Entry<Integer, List<Runnable>> entry : keyHoldActions.entrySet()) {
            int key = entry.getKey();
            if (glfwGetKey(Window.getWindow(), key) == GLFW_PRESS) {
                for (Runnable action : entry.getValue()) {
                    action.run();
                }
            }
        }    
    }
    
    // add key press action
    public static void onKeyPress(int key, Runnable action) {
        keyActions.computeIfAbsent(key, k -> new ArrayList<>()).add(action);
    }
    
    //add key hold action
    public static void onKeyHold(int key, Runnable action) {
    	keyHoldActions.computeIfAbsent(key, k -> new ArrayList<>()).add(action);
    }
    
    
}
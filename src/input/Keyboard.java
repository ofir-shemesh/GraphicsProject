package input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard {
    private static Map<Integer, List<Runnable>> keyActions = new HashMap<>();
    private static Map<Integer, List<Runnable>> keyHoldActions = new HashMap<>();
    
    public static void init() {
    	glfwSetKeyCallback(Window.getWindow(), (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                runActions(key);
            }
        });
    }
    
    public static void tick() {
    	for (Map.Entry<Integer, List<Runnable>> entry : keyHoldActions.entrySet()) {
            int key = entry.getKey();
            if (glfwGetKey(Window.getWindow(), key) == GLFW_PRESS) {
                for (Runnable action : entry.getValue()) {
                    action.run();
                }
            }
        }    }
    
    public static void onKeyPress(int key, Runnable action) {
        keyActions.computeIfAbsent(key, k -> new ArrayList<>()).add(action);
    }
    
    public static void onKeyHold(int key, Runnable action) {
    	keyHoldActions.computeIfAbsent(key, k -> new ArrayList<>()).add(action);
    }

    private static void runActions(int key) {
        List<Runnable> actions = keyActions.get(key);
        if (actions != null) {
            for (Runnable r : actions) {
                r.run();
            }
        }
    }
}
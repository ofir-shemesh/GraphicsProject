package main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import color.Color;

/**
 * Manages the GLFW window and OpenGL context.
 * 
 * Designed as a single static window instance for the application.
 * 
 * - must be initialized before use
 * - must be cleaned after use
 */
public class Window {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	
	private static final String TITLE = "Graphics Project";
	private static final Color backgroundColor = new Color(0.1f, 0.15f, 0.3f, 1.0f);
    
	/**
	 * an openGL representation of the window
	 */
	private static long window;
    
	/**
	 * Initializes the GLFW windowing system and configures the OpenGL context.
	 * 
	 * This method performs all required setup before rendering can begin, including:
	 * - initializing GLFW and setting up error reporting
	 * - configuring OpenGL version (3.3 core profile)
	 * - creating the application window and OpenGL context
	 * - making the context current for rendering calls
	 * - enabling vertical synchronization (V-Sync)
	 * - enabling depth testing and configuring blending behavior
	 * - setting the default background clear color
	 * - displaying the window on screen
	 * 
	 * This must be called once before entering the main render loop.
	 */
	public static void init() {
		// init GLFW
		GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Setting Window Hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // for macOS

        // Create Window
        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(window); //make openGL calls affect this window
        glfwSwapInterval(1); // v-sync
        glfwShowWindow(window);

        GL.createCapabilities();
        
        glEnable(GL_DEPTH_TEST);//enables depth sorting
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // setup transparency
        glDepthFunc(GL_LESS);//hides a pixel that behind the current one
        
        //setup clear color
        glClearColor(backgroundColor.getR(),
        			backgroundColor.getG(),
        			backgroundColor.getB(),
        			backgroundColor.getA());
        
	}
	
	/**
	 * @return whether the window is being closed (e.g. by pressing the red exit button)
	 */
	public static boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	/**
	 * @return the openGL window representation
	 */

	public static long getWindow() {
		return window;
	}
	
	/**
	 * run this before every iteration of the game loop
	 * 
	 * - clears color buffer
	 * - clears depth buffer
	 */
	public static void loop_before() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	/**
	 * run this before after iteration of the game loop
	 * 
	 * - moves to the next frame
	 * - gets input
	 */
	public static void loop_after() {
        glfwSwapBuffers(window);
        glfwPollEvents();
	}
	
	public static void clean() {
		glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
	}
}

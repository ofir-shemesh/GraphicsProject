package main;

import entityRaw.*;
import factory.RawModelFactory;
import color.Color;
import color.ColorGradient;
import color.GradientElement;
import input.Keyboard;
import input.Mouse;
import player.Player;
import sky.Sky;
import sky.Time;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import org.joml.Vector2f;
import org.joml.Vector3f;

import camera.Camera;
import entity.Renderable;
import entityData.*;
import utils.MyMath;

public class Main {
	
	private static Camera camera;
	
	private static Renderable player_rend;
	private static Player player;
	
	private static Renderable sky_rend;
	private static Sky sky;
	
	private static Renderable floor_rend;
	
	private static Renderable barrel_rend;
	
	// Player & Camera
	
	private static void initRenderable() {
		RawModel model = RawModelFactory.OBJModel("res/models/bunny.obj");
		ShaderProgram program = new ShaderProgram("res/shaders/player/vert.vert", "res/shaders/player/frag.frag");
		
		player_rend = new Renderable(model, program, new Texture[] {});
	}
	
	private static void run() {
		init();
		loop();
		clean();
	}
	
	private static void initCamera() {
		camera = new Camera(new Vector3f().zero(),
				0.0f, 0.0f, 0.0f,
				0.1f, 10.0f, MyMath.pi/6, 1.0f);
		
		Mouse.onMovement((prevx, prevy, currx, curry) -> {
			camera.increasePitch((currx-prevx)*0.005f);
			camera.increaseYaw((curry-prevy)*0.005f);
		});
	}
	
	private static void initMovement() {
		player = new Player(new Vector3f().zero(),
							MyMath.pi/2, MyMath.pi, MyMath.pi/2);
		float speed = 0.01f;
		
		
		
		Keyboard.onKeyHold(GLFW_KEY_A, () -> {
			player.move(speed, -camera.getPitch() + MyMath.pi / 2);
		});
		
		Keyboard.onKeyHold(GLFW_KEY_S, () -> {
			player.move(speed, -camera.getPitch() + MyMath.pi);
		});
		
		Keyboard.onKeyHold(GLFW_KEY_D, () -> {
			player.move(speed, -camera.getPitch() + -MyMath.pi / 2);
		});
		
		Keyboard.onKeyHold(GLFW_KEY_W, () -> {
			player.move(speed, -camera.getPitch() + 0);
		});
		
		Runnable setPlayerUniforms = () -> {
			player_rend.getShaderProgram().editUniform("translationTrans", player.getTranslation());
			player_rend.getShaderProgram().editUniform("rotationTrans", player.getRotation());			
		};
		
		Runnable setCameraPosition = () -> {
			camera.follow(player.getPosition());
		};
		
		Runnable setCameraUniforms = () -> {
			player_rend.getShaderProgram().editUniform("camTrans", camera.getTotalTransformation());
		};
		
		Mouse.onMovement((prevx, prevy, currx, curry) -> {
			setCameraPosition.run();
		});
		
		setCameraPosition.run();
		setPlayerUniforms.run();
		setCameraUniforms.run();
		
		player.addPostPosEdit(setPlayerUniforms);
		player.addPostPosEdit(setCameraPosition);
		
		camera.addPostPosEdit(setCameraUniforms);
	}
	
	// Sky 
	
	private static void initStars() {
		Runnable setCameraUniforms = () -> {
			sky_rend.getShaderProgram().editUniform("camTrans", camera.getPersAngTrans());
		};
		
		setCameraUniforms.run();
		camera.addPostRotEdit(setCameraUniforms);
		
		
	}
	
	private static void initSkyRenderable() {
		RawModel model = RawModelFactory.screenQuad();
		ShaderProgram program = new ShaderProgram("res/shaders/sky/vert.vert", "res/shaders/sky/frag.frag");
		Texture texture = new Texture("res/textures/sky/skybox/", true);
		Texture[] textures = {texture};
		
		sky_rend = new Renderable(model, program, textures);
	}
	
	private static void initSky() {
		ColorGradient gradient_top = new ColorGradient(new GradientElement[] {
			    // Night — deep dark blue
			    new GradientElement(new Color(0.03f, 0.05f, 0.12f, 1.0f), 0.05f, 0.08f),
			    // Blue hour — faint violet-blue
			    new GradientElement(new Color(0.12f, 0.10f, 0.25f, 1.0f), 0.05f, 0.08f),
			    // Sunrise — purple to gold transition
			    new GradientElement(new Color(0.30f, 0.20f, 0.60f, 1.0f), 0.06f, 0.09f),
			    // Morning — light clear blue
			    new GradientElement(new Color(0.45f, 0.65f, 0.95f, 1.0f), 0.07f, 0.09f),
			    // Noon — bright pure sky blue
			    new GradientElement(new Color(0.25f, 0.55f, 1.00f, 1.0f), 0.07f, 0.09f),
			    // Afternoon — softer, slightly warm blue
			    new GradientElement(new Color(0.40f, 0.60f, 0.95f, 1.0f), 0.05f, 0.08f),
			    // Sunset — orange-pink blend
			    new GradientElement(new Color(0.75f, 0.35f, 0.50f, 1.0f), 0.05f, 0.08f),
			    // Evening — deep indigo
			    new GradientElement(new Color(0.15f, 0.20f, 0.45f, 1.0f), 0.05f, 0.08f)
			});

		ColorGradient gradient_bottom = new ColorGradient(new GradientElement[] {
		    // Night — dark horizon
		    new GradientElement(new Color(0.02f, 0.03f, 0.06f, 1.0f), 0.05f, 0.08f),
		    // Blue hour — cool dark blue
		    new GradientElement(new Color(0.10f, 0.12f, 0.20f, 1.0f), 0.05f, 0.08f),
		    // Sunrise — orange glow
		    new GradientElement(new Color(0.90f, 0.55f, 0.25f, 1.0f), 0.06f, 0.09f),
		    // Morning — pale soft blue
		    new GradientElement(new Color(0.65f, 0.80f, 0.95f, 1.0f), 0.07f, 0.09f),
		    // Noon — bright near-white blue
		    new GradientElement(new Color(0.55f, 0.85f, 1.00f, 1.0f), 0.07f, 0.09f),
		    // Afternoon — faint golden tint
		    new GradientElement(new Color(0.80f, 0.85f, 0.90f, 1.0f), 0.05f, 0.08f),
		    // Sunset — vivid orange-red
		    new GradientElement(new Color(0.95f, 0.40f, 0.20f, 1.0f), 0.05f, 0.08f),
		    // Evening — purple-gray horizon
		    new GradientElement(new Color(0.40f, 0.25f, 0.50f, 1.0f), 0.05f, 0.08f)
		});
		
		sky = new Sky(gradient_top, gradient_bottom);	
		
		Runnable updateSkyUniforms = () -> {
			sky.applyToShader(sky_rend.getShaderProgram());	
		};
		
		updateSkyUniforms.run();
		
		Time.addPostTick(updateSkyUniforms);
	}
	
	//Floor
	
	private static void initFloorRenderable() {
		RawModel model = RawModelFactory.quadFloor(new Vector3f(0.0f, -0.5f, 0.0f), new Vector2f(20.0f, 20.0f));
		ShaderProgram program = new ShaderProgram("res/shaders/floor/vert.vert", "res/shaders/floor/frag.frag");
		
		floor_rend = new Renderable(model, program, new Texture[] {});
		
		Runnable setCameraUniforms = () -> {
			floor_rend.getShaderProgram().editUniform("camTrans", camera.getTotalTransformation());
			floor_rend.getShaderProgram().editUniform("camTranslationTrans", camera.getTranslationTransformation());
		};
		
		setCameraUniforms.run();
		camera.addPostPosEdit(setCameraUniforms);
		
	}	
	
	//Barrel
	
	private static void initBarrelRenderable() {
		RawModel model = RawModelFactory.OBJModel("res/models/barrel.obj", true, true);
		ShaderProgram program = new ShaderProgram("res/shaders/barrel/vert.vert", "res/shaders/barrel/frag.frag");
		Texture texture = new Texture("res/textures/barrel/barrel.png", false);
		Texture[] textures = {texture};

		barrel_rend = new Renderable(model, program, textures);
		
		Runnable setCameraUniforms = () -> {
			barrel_rend.getShaderProgram().editUniform("camTrans", camera.getTotalTransformation());
		};
		
		setCameraUniforms.run();
		camera.addPostPosEdit(setCameraUniforms);
		
	}
	
	private static void init() {
		Window.init();
		Keyboard.init();
		Mouse.init();

		initCamera();

		initSkyRenderable();
		initSky();
		initStars();
		
		initFloorRenderable();
		
		initRenderable();
		initMovement();
		
		initBarrelRenderable();
	}
	
	private static void loop() {
		while (!Window.shouldClose()) { 
			Window.loop_before();
			

			Time.tick();
			sky_rend.render(true);
			
			floor_rend.render();
			
			player_rend.render();
			player.tick();
			
			barrel_rend.render();
			
			Keyboard.tick();
			
			Window.loop_after();			
		}
	}
	
	private static void clean() {
		Window.clean();
		Mouse.clean();
		
		player_rend.clean();
		sky_rend.clean();
		floor_rend.clean();
		barrel_rend.clean();
	}
	
	public static void main(String [] args) {
		run();
	}
}

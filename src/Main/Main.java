package main;

import entityRaw.*;
import factory.RawModelFactory;
import input.Keyboard;
import input.Mouse;
import player.Player;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import org.joml.Vector3f;

import camera.Camera;
import entity.Renderable;
import entityData.*;
import utils.MyMath;

public class Main {
	
	private static Camera camera;
	
	private static Renderable rend;
	private static Player player;
	
	private static void initRenderable() {
		RawModel model = RawModelFactory.OBJModel("res/models/bunny.obj");
		ShaderProgram program = new ShaderProgram("res/shaders/vert.vert", "res/shaders/frag.frag");
		
		rend = new Renderable(model, program, new Texture[] {});
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
			rend.getShaderProgram().editUniform("translationTrans", player.getTranslation());
			rend.getShaderProgram().editUniform("rotationTrans", player.getRotation());			
		};
		
		Runnable setCameraPosition = () -> {
			camera.follow(player.getPosition());
		};
		
		Runnable setCameraUniforms = () -> {
			rend.getShaderProgram().editUniform("camTrans", camera.getTotalTransformation());
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
	
	
	
	private static void init() {
		Window.init();
		Keyboard.init();
		Mouse.init();
		
		initCamera();
		initRenderable();
		initMovement();
	}
	
	private static void loop() {
		while (!Window.shouldClose()) { 
			Window.loop_before();
			
			rend.render(false);
			player.tick();
			
			Keyboard.tick();
			
			Window.loop_after();			
		}
	}
	
	private static void clean() {
		Window.clean();
		Mouse.clean();
	}
	
	public static void main(String [] args) {
		run();
	}
}

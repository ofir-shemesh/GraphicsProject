package coin;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import render.components.ShaderProgram;
import render.components.Texture;
import render.factory.RawModelFactory;
import render.raw.components.RawModel;
import render.scene.Renderable;
import utils.MyMath;

public class Coin {
	private static final float rotation_speed = 0.01f;
	private static final float harmonic_speed = 0.01f;
	
	private static final float harmonic_amplitude = 0.1f;
	private static final Matrix4f scale_trans = new Matrix4f().scale(0.5f);
	
	private float rotation_angle = 0.0f;
	private float harmonic_angle = 0.0f;
	
	private Vector3f base_position;
	private static final Matrix4f base_rotation = new Matrix4f().identity();
	
	private Renderable renderable;
	
	public Coin(Vector3f base_position) {
		this.base_position = new Vector3f(base_position);
		
		RawModel model = RawModelFactory.OBJModel("res/models/coin.obj");
		ShaderProgram program = new ShaderProgram("res/shaders/coin/vert.vert", "res/shaders/coin/frag.frag");
		
		this.renderable = new Renderable(model, program, new Texture[] {});
		renderable.getShaderProgram().editUniform("scaleTrans", scale_trans);

	}
	
	public void tick() {
		this.rotation_angle += rotation_speed;
		this.harmonic_angle += harmonic_speed;
		
		this.rotation_angle %= MyMath.pi * 2;
		this.harmonic_angle %= MyMath.pi * 2;
		
		renderable.getShaderProgram().editUniform("translationTrans", getTranslation());
		renderable.getShaderProgram().editUniform("rotationTrans", getRotation());
	}
	
	public Renderable getRenderable() {
		return this.renderable;
	}
	
	public Matrix4f getRotation() {
		Matrix4f transformation = new Matrix4f(base_rotation);
		transformation.rotateY(rotation_angle);
		return transformation;
	}
	
	public Matrix4f getTranslation() {
		float disp = (float) Math.sin(harmonic_angle);
		disp *= harmonic_amplitude;
		
		Vector3f translation_vector = new Vector3f(base_position).add(0.0f, disp, 0.0f);
		
		return new Matrix4f().translate(translation_vector);
	}
	
	public void render() {
		renderable.render();
	}
	
	public void clean() {
		renderable.clean();
	}
}

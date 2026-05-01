package player;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Player {
	private Vector3f position;
	public Matrix4f base_rotation;
	private float angle = 0.0f;
	private Runnable onPosChange = () -> {},
			onRotChange = () -> {};
	
	public Player(Vector3f position, 
			  float pitch, float yaw, float roll) {
		this.position = new Vector3f(position);
		this.base_rotation = new Matrix4f()
				 .rotateZ(roll)
				 .rotateX(yaw)
				 .rotateY(pitch);
	}
	
	public void move(float dx, float angle) {
		setAngle(angle);
		
		Vector3f dp = new Vector3f(0.0f, 0.0f, -1.0f).mul(dx).rotateY(angle);
		this.position = new Vector3f(position).add(dp);
		
		onPosChange.run();
	}
	
	public void addPostPosEdit(Runnable addition) {
		Runnable old = this.onPosChange;
		
		this.onPosChange = () -> {
			old.run();
			addition.run();
		};
		
	}
	
	public void addPostRotEdit(Runnable addition) {
		Runnable old = this.onRotChange;
		
		this.onRotChange = () -> {
			old.run();
			addition.run();
		};
		
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
		
		onRotChange.run();
	}
	
	public Vector3f getPosition() {
		return new Vector3f(position);
	}

	public Matrix4f getTranslation() {
		return new Matrix4f().translate(position);
	}

	public Matrix4f getRotation() {
		return new Matrix4f().rotateY(angle).mul(base_rotation);
	}
	
	public void tick() {

	}
}

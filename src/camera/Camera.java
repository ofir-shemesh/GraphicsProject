package camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import utils.MyMath;

public class Camera {
	private Vector3f position;
	private float pitch, yaw, roll;
	private float z_near, z_far, fov, aspectRatio;
	private float R = 5.0f;
	private Runnable onPosChange = () -> {},
			onRotChange = () -> {};
			
	//TODO: change camera input!
	public Camera(Vector3f position,
			float pitch, float yaw, float roll,
			float z_near, float z_far, float fov, float aspectRatio) {
		this.position = position;
		
		this.pitch = pitch;
		this.yaw = yaw;
		increaseYaw(0.0f);
		this.roll = roll;
		
		this.z_near = z_near;
		this.z_far = z_far;
		this.fov = fov;
		this.aspectRatio = aspectRatio;
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
		
	private void postRotEdit() {
		if (onRotChange != null) {
			onRotChange.run();
		}
	}
		
	private void postPosEdit() {
		if (onPosChange != null) {
			onPosChange.run();
		}
	}
	
	// Setters
	
	public void follow(Vector3f position) {
		Vector4f rVector4 = new Vector4f(0.0f, 0.0f, R, 1.0f).mul(getRotationTransformation().transpose());
		Vector3f rVector = new Vector3f(rVector4.x, rVector4.y, rVector4.z);
		
		
		this.setPosition(new Vector3f(position).add(rVector));
	}
	
	public void move(Vector3f dp) {
		setPosition(new Vector3f(this.position).add(dp));
	}
	
	public void setPosition(Vector3f p) {
		this.position.x = p.x;
		this.position.y = p.y;
		this.position.z = p.z;
		
		postPosEdit();
	}
	
	public void increaseRoll(float val) {
		this.roll += val;
		postRotEdit();
	}

	public void increasePitch(float val) {
		this.pitch += val;		
		postRotEdit();
	}
	
	public void increaseYaw(float val) {
		this.yaw += val;
		this.yaw = Math.clamp(yaw, -MyMath.pi / 2, MyMath.pi / 2);

		postRotEdit();
	}
	
	//Getters
	
	public Vector3f getPosition() {
		return new Vector3f(this.position);
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public float getRoll() {
		return this.roll;
	}
	
	public float getZfar() {
		return this.z_far;
	}
	
	public float getZnear() {
		return this.z_near;
	}
	
	public float getFOV() {
		return this.fov;
	}
	
	public float getAspectRatio() {
		return this.aspectRatio;
	}
	
	//Transformations
	
	private Matrix4f getTranslationTransformation() {
		return new Matrix4f().translation(new Vector3f(position).negate());
	}
	
	private Matrix4f getRotationTransformation() {
		//camera starts pointing in the negative z direction
		//rotation order
		//	z-roll then
		//	x-yaw then
		//	y-pitch
		return new Matrix4f().rotateZ(roll)
							 .rotateX(yaw)
							 .rotateY(pitch);
	}
	
	public Matrix4f getViewTransformation() {
		//translation then rotation
		return getRotationTransformation().mul(getTranslationTransformation());
	}
	
	public Matrix4f getProjectionTransformation() {
		return new Matrix4f().perspective(
				fov,
				aspectRatio,
				z_near,
				z_far);
	}
	
	
	public Matrix4f getPersAngTrans() {

		return getProjectionTransformation().mul(getRotationTransformation());
	}
	
	public Matrix4f getTotalTransformation() {
		//first rotation & translation then projection
		return getProjectionTransformation().mul(getViewTransformation());
	}
	
	public Matrix4f getTransformation(boolean translation, boolean rotation, boolean projection) {
		Matrix4f identity = new Matrix4f();
		Matrix4f translation_part = translation ? getTranslationTransformation() : identity;
		Matrix4f rotation_part = rotation ? getRotationTransformation() : identity;
		Matrix4f projection_part = projection ? getProjectionTransformation() : identity;
		
		return projection_part.mul(rotation_part).mul(translation_part);
	}
}

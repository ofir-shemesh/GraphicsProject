package camera;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import utils.MyMath;

/**
 * Represents a 3D camera in world space.
 *
 * The camera defines a transformation from world space to view space,
 * and supports perspective projection for rendering.
 *
 * It maintains position and orientation (pitch, yaw, roll),
 * and provides transformation matrices for rendering pipelines
 *
 * The camera can also notify listeners when its position or rotation changes.
 */
public class Camera {
	//camera position in space
	private Vector3f position;
	
	// orientation variables
	// yaw must be clamped between [-pi/2, pi/2]
	private float pitch, yaw, roll;
	// perspective variables
	private float z_near, z_far, fov, aspectRatio;
	
	/*
	 * Distance to followed object
	 */
	private float R = 5.0f;
	
	// Runnables to execute after position and rotation changed
	private final List<Runnable> posChangeListeners = new ArrayList<>();
	private final List<Runnable> rotChangeListeners = new ArrayList<>();

			
	public Camera(Vector3f position,
			float pitch, float yaw, float roll,
			float z_near, float z_far, float fov, float aspectRatio) {
		this.position = new Vector3f(position);
		
		this.pitch = pitch;
		this.yaw = 0.0f;
		increaseYaw(yaw);// used here to clamp the yaw value to the allowed range
		this.roll = roll;
		
		// perspective variables
		this.z_near = z_near;
		this.z_far = z_far;
		this.fov = fov;
		this.aspectRatio = aspectRatio;
	}
	
	/*
	 * This Sections handles the Runnables.
	 * the first functions add another functionality on top of the current one.
	 * the second one runs it (called from the functions that edit position, rotation)
	 */
	public void addPositionChangeListener(Runnable listener) {
	    posChangeListeners.add(listener);
	}

	public void addRotationChangeListener(Runnable listener) {
	    rotChangeListeners.add(listener);
	}
		
	private void notifyPositionChanged() {
	    for (Runnable r : posChangeListeners) {
	        r.run();
	    }
	}

	private void notifyRotationChanged() {
	    for (Runnable r : rotChangeListeners) {
	        r.run();
	    }
	}
	
	
	// Setters
	
	/**
	 * Positions the camera a distance R from targetPosition
	 * the direction is determined by yaw, pitch, roll so that the camera looks at target
	 */
	public void follow(Vector3f targetPosition) {
		//Vector from target to camera
		Vector4f rVector4 = new Vector4f(0.0f, 0.0f, R, 1.0f).mul(getRotationTransformation().transpose());
		Vector3f rVector = new Vector3f(rVector4.x, rVector4.y, rVector4.z);	
		
		this.setPosition(new Vector3f(targetPosition).add(rVector));
	}
	
	public void setPosition(Vector3f newPosition) {
		this.position.set(newPosition);
		
		notifyPositionChanged();
	}
	
	public void increaseRoll(float val) {
		this.roll += val;
		notifyRotationChanged();
	}

	public void increasePitch(float val) {
		this.pitch += val;		
		notifyRotationChanged();
	}
	
	public void increaseYaw(float val) {
		this.yaw += val;
		this.yaw = Math.clamp(yaw, -MyMath.pi / 2, MyMath.pi / 2);

		notifyRotationChanged();
	}
	
	//Getters

	//Position Getter
	
	public Vector3f getPosition() {
		return new Vector3f(this.position);
	}
	
	// Orientation Getters
	
	public float getPitch() {
		return this.pitch;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public float getRoll() {
		return this.roll;
	}

	
	//Transformation Getters
	
	/*
	 * Basic Transformations
	 * - Translation
	 * - Rotation
	 * - Perspective
	 */
	public Matrix4f getTranslationTransformation() {
		return new Matrix4f().translation(new Vector3f(position).negate());
	}
	
	public Matrix4f getRotationTransformation() {
		//camera starts pointing in the negative z direction
		//rotation order
		//	z-roll then
		//	x-yaw then
		//	y-pitch
		return new Matrix4f().rotateZ(roll)
							 .rotateX(yaw)
							 .rotateY(pitch);
	}

	public Matrix4f getProjectionTransformation() {
		return new Matrix4f().perspective(
				fov,
				aspectRatio,
				z_near,
				z_far);
	}
	
	/**
	 * Builds a transformation matrix by composing:
	 * Projection * Rotation * Translation
	 *
	 * Each component can be optionally included.
	 *
	 * Note: The multiplication order matters.
	 * The resulting matrix applies Translation first,
	 * then Rotation, then Projection.
	 *
	 * @param includeProjection whether to include the projection transform
	 * @param includeRotation whether to include the rotation transform
	 * @param includeTranslation whether to include the translation transform
	 * @return the composed transformation matrix
	 */
	public Matrix4f getTransformation(boolean includeProjection,
	                                  boolean includeRotation,
	                                  boolean includeTranslation) {
	    Matrix4f identity = new Matrix4f().identity();;

	    Matrix4f translationMatrix = includeTranslation ? getTranslationTransformation() : identity;
	    Matrix4f rotationMatrix = includeRotation ? getRotationTransformation() : identity;
	    Matrix4f projectionMatrix = includeProjection ? getProjectionTransformation() : identity;

	    return projectionMatrix.mul(rotationMatrix).mul(translationMatrix);
	}
	
	/*
	 * Useful Combinations
	 */
	
	/**
	 * Transforms from world space to clip space
	 *
	 * Useful for objects already defined in camera-relative space.
	 * or far far away objects like the sun
	 */
	
	public Matrix4f getViewProjectionMatrix() {
		return getTransformation(true, true, false);
	}
	
	/**
	 * Transforms from world space to clip space
	 * 
	 * Useful for regular (non-distant) objects
	 */
	
	public Matrix4f getModelViewProjectionMatrix() {
		return getTransformation(true, true, true);
	}
	
	/**
	 * clean
	 */
	
	public void clean() {
		
	}
	
}

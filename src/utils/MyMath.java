package utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;

public class MyMath {
	public final static float pi = (float) Math.PI;

	public static float smoothstep(float edge0, float edge1, float x) {
	    // Clamp x to 0..1
	    float t = (x - edge0) / (edge1 - edge0);
	    t = Math.max(0.0f, Math.min(1.0f, t));
	    // Smoothstep formula
	    return t * t * (3.0f - 2.0f * t);
	}
	

	//Conversions
	private static Vector3f convertVector(AIVector3D input_vec) {
		return new Vector3f(input_vec.x(), input_vec.y(), input_vec.z());
	}
	
	private static Quaternionf convertQuat(AIQuaternion input_quat) {
		return new Quaternionf(input_quat.x(), input_quat.y(), input_quat.z(), input_quat.w());
	}
	
	public static Matrix4f convertMat(AIMatrix4x4 m) {
	    Matrix4f mat = new Matrix4f();

	    mat.m00(m.a1()); mat.m10(m.a2()); mat.m20(m.a3()); mat.m30(m.a4());
	    mat.m01(m.b1()); mat.m11(m.b2()); mat.m21(m.b3()); mat.m31(m.b4());
	    mat.m02(m.c1()); mat.m12(m.c2()); mat.m22(m.c3()); mat.m32(m.c4());
	    mat.m03(m.d1()); mat.m13(m.d2()); mat.m23(m.d3()); mat.m33(m.d4());

	    return mat;
	}
	

	//Interpolations
	
	public static Vector3f interpolateVectorKeys(AIVectorKey.Buffer keys, int numKeys, double duration, float portion) {
		double prev_time = 0.0;
		double time = portion * duration;
		
		AIVector3D prev_vector = keys.get(0).mValue();
		Vector3f vector = convertVector(prev_vector);
		
		for (int k = 1; k < numKeys; k++) {
            AIVectorKey key = keys.get(k);
            AIVector3D next_vector = key.mValue();
            double next_time = key.mTime();
            
            if (next_time >= time) {
                float fraction = (float) ((time-prev_time) / (next_time-prev_time));
                
                Vector3f prev_vectorf = convertVector(prev_vector);
                
                Vector3f next_vectorf = convertVector(next_vector);
                
                prev_vectorf.lerp(next_vectorf, fraction, vector);
                return vector;
            }else {
            	prev_time = next_time;
            	prev_vector = next_vector;
            	
            }            
        }
		return vector;
	}
	
	public static Quaternionf interpolateQuatKeys(AIQuatKey.Buffer keys, int numKeys, double duration, float portion) {
		double prev_time = keys.get(0).mTime();
		double time = prev_time + portion * duration;
		
		AIQuaternion prev_quat = keys.get(0).mValue();
		Quaternionf quat = convertQuat(prev_quat);
		
		for (int k = 1; k < numKeys; k++) {
			AIQuatKey key = keys.get(k);
			AIQuaternion next_position = key.mValue();
            double next_time = key.mTime();
            
            if (next_time >= time) {
                float fraction = (float) ((time-prev_time) / (next_time-prev_time));
                
                Quaternionf prev_quatf = convertQuat(prev_quat);
                
                Quaternionf next_quatf = convertQuat(next_position);
                                
                prev_quatf.slerp(next_quatf, fraction, quat);
                
                return quat;
            }else {
            	prev_time = next_time;
            	prev_quat = next_position;
            	
            }            
        }
		return quat;
	}


	public static float clamp(float val, float min, float max) {
		return (float) Math.min(Math.max(val, min), max);
	}
}

package entityData;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import utils.Utils;
import color.Color;

public class ShaderProgram {
	int programID;
	
	public ShaderProgram(String vertexFile, String fragmentFile) {
		programID = createShaderProgram(vertexFile, fragmentFile);
	}
	
	public void clean() {
		glDeleteProgram(programID);
	}
	
	
	//create Shaders
	private static int createShader(String source, int TYPE) {
		int shader = glCreateShader(TYPE);
        glShaderSource(shader, source);
        glCompileShader(shader);
        
        checkShader(shader);
        return shader;
	}	

	private static void checkShader(int shader) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(shader));
            throw new RuntimeException("Shader compile error");
        }
    }

    private static void checkProgram(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(program));
            throw new RuntimeException("Program link error");
        }
    }
	
	private static int createShaderProgram(int vertexShader, int fragmentShader) {
		int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        
        checkProgram(shaderProgram);
        return shaderProgram;
	}	

	private static int createShaderProgram(String vertexFile, String fragmentFile) {
		//Read Shaders
		String vertexSource = Utils.readFromFile(vertexFile);
		String fragmentSource = Utils.readFromFile(fragmentFile);
		
		//Create Vertex Shader
		int vertexShader = createShader(vertexSource, GL_VERTEX_SHADER);
        int fragmentShader = createShader(fragmentSource, GL_FRAGMENT_SHADER);
      
        //Create Program
		int shaderProgram = createShaderProgram(vertexShader, fragmentShader);
        
		//TODO: uniforms
		
		//Delete Unnecessary Shaders
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        
        return shaderProgram;

	}

	public int getProgramID() {
		return this.programID;
	}
	
	//Uniform stuff

	public void editUniform(String name, Matrix4f val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		
		float[] matrixArr = new float[16];

		val.get(matrixArr);
		if (loc != -1)
			glUniformMatrix4fv(loc, false, matrixArr);
		
		glUseProgram(0);
	}
	
	public void editUniform(String name, Matrix4f[] values) {
	    for (int i = 0; i < values.length; i++) {
	    	editUniform(name + "[" + i + "]", values[i]);
	    }
	}
	
	public void editUniform(String name, float val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		if (loc != -1)
			glUniform1f(loc, val);
		
		glUseProgram(0);
	}
	
	public void editUniform(String name, int val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		if (loc != -1)
			glUniform1i(loc, val);
		
		glUseProgram(0);
	}
	
	public void editUniform(String name, boolean val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		int int_val = val ? 1 : 0;
		
		if (loc != -1)
			glUniform1i(loc, int_val);
		
		glUseProgram(0);
	}
	
	
	public void editUniform(String name, Color val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		if (loc != -1)
			glUniform4f(loc, val.getR(), val.getG(), val.getB(), val.getA());
		
		glUseProgram(0);
	}

	public void editUniform(String name, Vector2f val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		if (loc != -1)
			glUniform2f(loc, val.x, val.y);
		
		glUseProgram(0);
	}
	
	public void editUniform(String name, Vector3f val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		if (loc != -1)
			glUniform3f(loc, val.x, val.y, val.z);
		
		glUseProgram(0);
	}
	
	public void editUniform(String name, Vector4f val) {
		glUseProgram(programID);
		
		int loc = glGetUniformLocation(programID, name);
		if (loc != -1)
			glUniform4f(loc, val.x, val.y, val.z, val.w);			
		
		
		glUseProgram(0);
	}
}


	package render.scene;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.List;

import main.Window;
import render.components.Model;
import render.components.ShaderProgram;
import render.components.Texture;
import render.raw.components.RawModel;
import utils.FBO;

/**
 * Represents a renderable object in the scene.
 *
 * A Renderable encapsulates all GPU-related resources required to draw an object,
 * - its geometry (Model)
 * - shader program
 * - associated textures.
 *
 * in the future should support multiple shader programs & models and allow switching
 * between them.
 *
 * This class does not manage transformations directly; it assumes that any
 * required transformation matrices (e.g., Model-View-Projection) are 
 * provided to the shader before rendering.
 *
 * The lifecycle of a Renderable includes explicit resource cleanup via clean(),
 * which must be called to release GPU memory when the object is no longer needed.
 */

//TODO: add support for switching shader programs & models

public class Renderable {
	
	private List<Model> models = new ArrayList<>();
	private List<ShaderProgram> shaderPrograms = new ArrayList<>();
	private List<Texture> textures = new ArrayList<>();
	
	private int currentShaderProgram, currentModel;
	
	
	public Renderable(RawModel rawModel,
			  ShaderProgram shaderProgram,
			  Texture[] textures) {
		
		//initialize Render Components
		this.models.add(new Model(rawModel));
		this.shaderPrograms.add(shaderProgram);
		for (Texture texture : textures) {
			this.textures.add(texture);
		}
		
		//set current program, model
		this.currentShaderProgram = 0;
		this.currentModel = 0;
		
		// updates the texture uniforms in the shader Program
		// always according to the naming convention texSam0, texSam1...
		for (int i = 0; i < textures.length; i++) {
			shaderProgram.editUniform("texSam" + i, i);
		}
	}
	
	// Getters
	public ShaderProgram getShaderProgram() {
		return shaderPrograms.get(currentShaderProgram);
	}
	
	
	/**
	 * Renders the object using default settings (non-GUI mode).
	 */
	public void render() {
		render(false);
	}
	
	/**
	 * Renders the object.
	 *
	 * @param GUI if true, disables depth testing so the object is rendered
	 *            as a screen-space element (e.g. UI)
	 */
	
	public void render(boolean GUI) {
		// --- GUI  setup ---
		if (GUI) {
			glDepthMask(false); // prevent depth writes
			glDisable(GL_DEPTH_TEST); // disable depth testing
		}
		
		// --- Render ---
		
		// --- Binding the Shader ---
		glUseProgram(getShaderProgram().getProgramID());
		glEnable(GL_BLEND);
		
		// --- Bind textures ---
	    // Each texture is assigned to a texture unit: GL_TEXTURE0 + i
	    
		for (int i = 0; i < textures.size(); i++) {
			Texture texture = textures.get(i);
			int texID = texture.getTexID();

			glActiveTexture(GL_TEXTURE0+i);
			
			int type = texture.isCubeMap() ? GL_TEXTURE_CUBE_MAP : GL_TEXTURE_2D;
			
            glBindTexture(type, texID);		
		}

		// --- Bind mesh (VAO) ---
		glBindVertexArray(models.get(currentModel).getVAOID());
		
		// --- Draw Call ---
		glDrawElements(GL_TRIANGLES, models.get(currentModel).getNumIndices(), GL_UNSIGNED_INT, 0);
		
		// --- Cleanup  ---
		
		glUseProgram(0);
		glBindVertexArray(0);
		
		
		// --- Restore GUI setup ---
		if (GUI) {
			glDepthMask(true);
			glEnable(GL_DEPTH_TEST);
		}
	}
	
	/**
	 * Renders the object into a Framebuffer Object (FBO).
	 *
	 * This is used for off-screen rendering 
	 *
	 * @param fbo the FBO to render into
	 * @param GUI whether GUI rendering rules should apply (as in render)
	 */	
	public void renderFBO(FBO fbo, boolean GUI) {
		
	    // Bind FBO and set viewport to match its resolution

		glBindFramebuffer(GL_FRAMEBUFFER, fbo.getFBO());
		glViewport(0, 0, fbo.getWidth(), fbo.getHeight());

		// render
		
		render(GUI);
		
	    // Restore default framebuffer (screen)

		glViewport(0, 0, Window.WIDTH, Window.HEIGHT);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	/**
	 * Frees all GPU resources associated with this Renderable.
	 *
	 * Should be called when the object is no longer needed to prevent
	 * GPU memory leaks.
	 */
	public void clean() {

	    // Delete model VAOs
	    for (Model model : models) {
	        model.clean(); // assumes VAO/VBO deletion inside Model
	    }

	    // Delete textures
	    for (Texture texture : textures) {
	        texture.clean();
	    }

	    // Delete shader programs
	    for (ShaderProgram shader : shaderPrograms) {
	        shader.clean(); // assumes glDeleteProgram inside
	    }
	}
}

	package entity;

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

import entityData.Model;
import entityData.ShaderProgram;
import entityData.Texture;
import main.Window;
import entityRaw.RawModel;
import utils.FBO;

public class Renderable {
	
	private List<Model> models = new ArrayList<>();
	private List<ShaderProgram> shaderPrograms = new ArrayList<>();
	private List<Texture> textures = new ArrayList<>();
	
	int currentShaderProgram, currentModel;
	
	public ShaderProgram getShaderProgram() {
		return shaderPrograms.get(currentShaderProgram);
	}
	
	public Renderable(RawModel rawModel,
			  ShaderProgram shaderProgram,
			  Texture[] textures) {
	this.models.add(new Model(rawModel));
	this.shaderPrograms.add(shaderProgram);
	for (Texture texture : textures) {
		this.textures.add(texture);
	}
	
	this.currentShaderProgram = 0;
	this.currentModel = 0;
	
	for (int i = 0; i < textures.length; i++) {
		shaderProgram.editUniform("texSam" + i, i);
	}
}
	public void render() {
		render(false);
	}
	
	public void render(boolean GUI) {
		if (GUI) {
			glDepthMask(false);
			glDisable(GL_DEPTH_TEST);
		}
		
		//Render
		
		glUseProgram(getShaderProgram().getProgramID());
		glEnable(GL_BLEND);
		
		for (int i = 0; i < textures.size(); i++) {
			Texture texture = textures.get(i);
			int texID = texture.getTexID();

			glActiveTexture(GL_TEXTURE0+i);
			
			int type = texture.isCubeMap() ? GL_TEXTURE_CUBE_MAP : GL_TEXTURE_2D;
			
            glBindTexture(type, texID);		
		}

		glBindVertexArray(models.get(currentModel).getVAOID());
		
		glDrawElements(GL_TRIANGLES, models.get(currentModel).getNumIndices(), GL_UNSIGNED_INT, 0);
				
		glUseProgram(0);
		glBindVertexArray(0);
		
		if (GUI) {
			glDepthMask(true);
			glEnable(GL_DEPTH_TEST);
		}
	}
	
	public void renderFBO(FBO fbo, boolean GUI) {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo.getFBO());
		glViewport(0, 0, fbo.getWidth(), fbo.getHeight()); // Match the FBO size

		render(GUI);
		
		glViewport(0, 0, Window.WIDTH, Window.HEIGHT);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void clean() {
		//TODO: complete
	}
}

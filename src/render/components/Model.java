package render.components;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

import java.util.ArrayList;
import java.util.List;

import render.raw.buffers.RawFloatBuffer;
import render.raw.buffers.RawIntBuffer;
import render.raw.components.RawModel;
import utils.Utils;

/**
 * Represents a GPU-side 3D model used for rendering.
 *
 * A Model encapsulates all OpenGL resources required to draw a mesh,
 * including the Vertex Array Object (VAO), Element Buffer Object (EBO),
 * and multiple Vertex Buffer Objects (VBOs) for vertex attributes.
 *
 * This class does not handle materials, shaders, or textures directly;
 * it only represents the geometric component of a renderable object.
 *
 * The Model must be explicitly cleaned using {@code clean()}
 */
public class Model {
	private int vao, ebo;
	private List<Integer> bufferIDs;
	
	private int num_indices;
	private int num_vertices;
	
	// --- Constructor & Helper Methods ---
	
	/**
	 * This method create & Binds a Float VBO
	 *  
	 * @param buffer - the RawFloatBuffer that contains the data
	 * @param attribIndex - the current available attribute index
	 * @return updated attribIndex
	 */
	private int createFloatBuffer(RawFloatBuffer buffer, int attribIndex) {
	    int bo = glGenBuffers();
	    bufferIDs.add(bo);

	    glBindBuffer(GL_ARRAY_BUFFER, bo);
	    glBufferData(GL_ARRAY_BUFFER,
	            	 Utils.createFloatBuffer((float[]) buffer.getArr()),
	            	 GL_STATIC_DRAW);

	    int[] sizes = buffer.getSizes();
	    int stride = buffer.getStride();
	    int offset = 0;

	    for (int size : sizes) {
	        glVertexAttribPointer(
	                attribIndex,
	                size,
	                GL_FLOAT,
	                false,
	                stride * Float.BYTES,
	                offset * Float.BYTES);

	        glEnableVertexAttribArray(attribIndex);

	        offset += size;
	        attribIndex++;
	    }

	    return attribIndex;
	}
	
	/**
	 * This method create & Binds a Float VBO
	 *  
	 * @param buffer - the RawIntBuffer that contains the data
	 * @param attribIndex - the current available attribute index
	 * @return updated attribIndex
	 */
	private int createIntBuffer(RawIntBuffer buffer, int attribIndex) {
	    int bo = glGenBuffers();
	    bufferIDs.add(bo);

	    glBindBuffer(GL_ARRAY_BUFFER, bo);
	    glBufferData(GL_ARRAY_BUFFER,
	            Utils.createIntBuffer((int[]) buffer.getArr()),
	            GL_STATIC_DRAW);

	    int[] sizes = buffer.getSizes();
	    int stride = buffer.getStride();
	    int offset = 0;

	    for (int size : sizes) {
	        glVertexAttribIPointer(
	                attribIndex,
	                size,
	                GL_INT,
	                stride * Integer.BYTES,
	                offset * Integer.BYTES
	        );

	        glEnableVertexAttribArray(attribIndex);

	        offset += size;
	        attribIndex++;
	    }

	    return attribIndex;
	}
	
	/**
	 * Constructor
	 * 
	 * constructs a Model from a RawModel
	 */
	public Model(RawModel rawModel) {
		this.num_indices = rawModel.indices.length;
		this.num_vertices = rawModel.getNumVertices();

		this.vao = glGenVertexArrays();
		this.ebo = glGenBuffers();
		this.bufferIDs = new ArrayList<>();
		
		glBindVertexArray(vao);
        
        // Indices Array
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Utils.createIntBuffer(rawModel.indices), GL_STATIC_DRAW);
                
        // attribIndex tracks the current vertex attribute location across all buffers (float and int)
        int attribIndex = 0;
        
        // Create Float Attribute Buffers 
        for (RawFloatBuffer buffer : rawModel.float_buffers) {
            attribIndex = createFloatBuffer(buffer, attribIndex);
        }
        
        
        // Create Int Attribute Buffers
        for (RawIntBuffer buffer : rawModel.int_buffers) {
            attribIndex = createIntBuffer(buffer, attribIndex);
        }
        
        // Cleanup
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
	}
	
	//update data
	public static void updateFloatBufferData(int id, float[] values) {
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferData(GL_ARRAY_BUFFER, Utils.createFloatBuffer(values), GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);		
	}
	
	public static void updateIntBufferData(int id, int[] values) {
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferData(GL_ARRAY_BUFFER, Utils.createIntBuffer(values), GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);		
	}
	
	//TODO: make this with less assumptions
	public void updateFloatBufferData(RawModel data) {
		for (int i = 0; i < bufferIDs.size(); i++) {
			glBindBuffer(GL_ARRAY_BUFFER, bufferIDs.get(i));
			
			glBufferData(GL_ARRAY_BUFFER, Utils.createFloatBuffer(data.float_buffers.get(i).getArr()), GL_STATIC_DRAW);
		}
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	// Getters
	
	public int getVAOID() {
		return this.vao;
	}
	
	public int getEBOID() {
		return this.ebo;
	}
	
	public int getNumIndices() {
		return num_indices;
	}
	
	public int getNumVertices() {
		return this.num_vertices;
	}
	
	// Clean
	
	public void clean() {
		for (int bo : bufferIDs) {			
			glDeleteBuffers(bo);
		}
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
	}
}

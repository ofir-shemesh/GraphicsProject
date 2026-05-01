package entityData;

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

import utils.MyFloatBuffer;
import utils.MyIntBuffer;
import utils.Utils;
import entityRaw.RawModel;

public class Model {
	private int vao, ebo;
	private List<Integer> bufferIDs;
	
	private int num_indices;
	private int num_vertices;
	
	public Model(RawModel rawModel) {
		this.vao = glGenVertexArrays();
		this.ebo = glGenBuffers();
		this.num_indices = rawModel.indices.length;
		this.num_vertices = rawModel.getNumVertices();
		
		this.bufferIDs = new ArrayList<>();
		
		glBindVertexArray(vao);
        
        //Indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Utils.createIntBuffer(rawModel.indices), GL_STATIC_DRAW);
        
        //TODO: less code duplication
        
        int attribCounter = 0;
        for (int i = 0; i < rawModel.float_buffers.size(); i++) {
        	MyFloatBuffer buffer = rawModel.float_buffers.get(i);
        	
        	int bo = glGenBuffers();
        	bufferIDs.add(bo);
            glBindBuffer(GL_ARRAY_BUFFER, bo);
        	
        	
    		float[] float_arr = (float[]) buffer.getArr();
    		glBufferData(GL_ARRAY_BUFFER, Utils.createFloatBuffer(float_arr), GL_STATIC_DRAW);
    		
    		int[] sizes = buffer.getSizes();
    		int stride = buffer.getSum();
    		
    		int offset = 0;
    		
    		for (int j = 0; j < sizes.length; j++) {
    			int size = sizes[j];

                glVertexAttribPointer(attribCounter, size, GL_FLOAT, false, stride * Float.BYTES, offset * Float.BYTES);
                offset += size;
                glEnableVertexAttribArray(attribCounter);
                attribCounter++;
    		}
        	 
            
        }
        
        
        for (int i = 0; i < rawModel.int_buffers.size(); i++) {
        	MyIntBuffer buffer = rawModel.int_buffers.get(i);
        	
        	int bo = glGenBuffers();
        	bufferIDs.add(bo);
            glBindBuffer(GL_ARRAY_BUFFER, bo);
        	
        	
    		int[] int_arr = (int[]) buffer.getArr();
    		
    		glBufferData(GL_ARRAY_BUFFER, Utils.createIntBuffer(int_arr), GL_STATIC_DRAW);
    		
    		int[] sizes = buffer.getSizes();
    		int stride = buffer.getSum();
    		
    		int offset = 0;
    		
    		for (int j = 0; j < sizes.length; j++) {
    			int size = sizes[j];
        		
    			glVertexAttribIPointer(attribCounter, size, GL_INT, stride * Integer.BYTES, offset * Integer.BYTES);
                offset += size;
                glEnableVertexAttribArray(attribCounter);
                attribCounter++;
    		}
        	 
            
        }
        
        
        //close
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
	}
	
	//TODO: remove from Loader
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
	
	//getters
	
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
	
	//clean
	
	public void clean() {
		for (int bo : bufferIDs) {			
			glDeleteBuffers(bo);
		}
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
	}
}

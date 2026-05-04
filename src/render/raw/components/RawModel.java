package render.raw.components;

import java.util.ArrayList;
import java.util.List;

import render.raw.buffers.RawFloatBuffer;
import render.raw.buffers.RawIntBuffer;

/**
 * Represents raw 3D model data stored in CPU memory.
 *
 * A RawModel contains 
 * - vertex attribute data stored in float, int buffers,
 * 	 (allows custom number of buffers)
 * - index array defining how vertices are connected into primitives.
 *
 * This structure is typically used as an intermediate format before
 * uploading data to the GPU (e.g., VAO/VBO creation in OpenGL).
 *
 * This class does not perform any GPU operations and is intended
 * as an intermediate representation for mesh geometry.
 */

public class RawModel {
	public final int[] indices;
	public final List<RawFloatBuffer> float_buffers;
	public final List<RawIntBuffer> int_buffers;
	
	public RawModel(int[] indices, List<RawFloatBuffer> float_buffers) {
		this(indices, float_buffers, new ArrayList<>());
	}

	public RawModel(int[] indices, List<RawFloatBuffer> float_buffers, List<RawIntBuffer> int_buffers) {
		this.indices = indices;
		this.float_buffers = float_buffers;
		this.int_buffers = int_buffers;
		
	}

	public int getNumVertices() {
		return 	this.float_buffers.get(0).getArr().length/this.float_buffers.get(0).getSizes()[0];
	}

}
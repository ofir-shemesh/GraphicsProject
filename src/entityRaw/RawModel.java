package entityRaw;

import java.util.ArrayList;
import java.util.List;

import utils.MyFloatBuffer;
import utils.MyIntBuffer;

public class RawModel {
	public final int[] indices;
	public final List<MyFloatBuffer> float_buffers;
	public final List<MyIntBuffer> int_buffers;
	
	public RawModel(int[] indices, List<MyFloatBuffer> float_buffers) {
		this(indices, float_buffers, new ArrayList<>());
	}

	public RawModel(int[] indices, List<MyFloatBuffer> float_buffers, List<MyIntBuffer> int_buffers) {
		this.indices = indices;
		this.float_buffers = float_buffers;
		this.int_buffers = int_buffers;
		
	}

	public int getNumVertices() {
		return 	this.float_buffers.get(0).getArr().length/this.float_buffers.get(0).getSizes()[0];
	}

}
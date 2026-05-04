package render.raw.buffers;

/**
 * Represents a raw integer vertex buffer with attribute layout information.
 *
 * This class stores an integer array along with a description of how the data
 * is structured into one or more interleaved vertex attributes.
 *
 * The {@code sizes} array defines the number of components for each attribute.
 * For example, sizes = {3, 1} means each vertex consists of:
 * - 3 integers for the first attribute
 * - 1 integer for the second attribute
 *
 * The total number of components per vertex (stride) is given by {@link #getStride()}.
 *
 *
 * It is intended as a CPU-side representation before uploading data to the GPU.
 */
public class RawIntBuffer {
	private final int[] arr;
	private final int[] sizes;
	
	
	/**
	 * Constructor
	 * 
	 * constructs the RawIntBuffer from the array of data and sizes of the individual attributes
	 * @param arr - the data
	 * @param sizes - the sizes of the individual attributes
	 */
	public RawIntBuffer(int[] arr, int[] sizes) {
		this.arr = arr;
		this.sizes = sizes;
	}
	
	/**
	 * Constructor
	 * 
	 * constructs a RawIntBuffer that contains one attribute from the array of
	 * data and the size of the attribute
	 * 
	 * @param arr - the data
	 * @param sizes - the sizes of the attribute
	 */
	public RawIntBuffer(int[] arr, int size) {
		this(arr, new int[]{size});
	}
	
	// --- Getters ---
	
	/**
	 * @return the total number of components per vertex (stride)
	 */ 
	public int getStride() {
		int sum = 0;

		for (int v : sizes) {
		    sum += v;
		}
		
		return sum;
	}
	
	
	public int[] getArr() {
		return this.arr;
	}
	
	public int[] getSizes() {
		return this.sizes;
	}
}

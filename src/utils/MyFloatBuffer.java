package utils;

public class MyFloatBuffer {
	private float[] arr;
	private int[] sizes;
	
	public MyFloatBuffer(float[] arr, int[] sizes) {
		this.arr = arr;
		this.sizes = sizes;
	}
	
	public MyFloatBuffer(float[] arr, int size) {
		this(arr, new int[]{size});
	}
	
	public int getSum() {
		int sum = 0;

		for (int v : sizes) {
		    sum += v;
		}
		
		return sum;
	}
	
	public float[] getArr() {
		return this.arr;
	}
	
	public int[] getSizes() {
		return this.sizes;
	}
}

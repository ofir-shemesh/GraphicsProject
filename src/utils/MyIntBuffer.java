package utils;

public class MyIntBuffer {
	private int[] arr;
	private int[] sizes;
	
	public MyIntBuffer(int[] arr, int[] sizes) {
		this.arr = arr;
		this.sizes = sizes;
	}
	
	public MyIntBuffer(int[] arr, int size) {
		this(arr, new int[]{size});
	}
	
	public int getSum() {
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

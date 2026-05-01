package color;

public class Color {
	private float r, g, b, a;
	
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		//commit4
	}
	
	//Getters
	
	public float getR() { 
		return this.r;
	}
	
	public float getG() { 
		return this.g;
	}
	
	public float getB() { 
		return this.b;
	}
	
	public float getA() { 
		return this.a;
	}
	
	//Setters
	
	public void setR(float val) {
		this.r = val;
	}

	public void setG(float val) {
		this.g = val;
	}

	public void setB(float val) {
		this.b = val;
	}

	public void setA(float val) {
		this.a = val;
	}
}

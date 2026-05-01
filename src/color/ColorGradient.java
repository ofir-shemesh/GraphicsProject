package color;

public class ColorGradient {
	GradientElement[] elements;
	
	public ColorGradient(GradientElement[] elements) {
		this.elements = elements;
	}
	
	public float getTotalTime() {
		float sum = 0.0f;
		
		for (GradientElement element : elements) {
			sum += element.getTotalTime();
		}

		return sum;
	}
	
	private float interpolate(float val1, float val2, float t) {
		return val1*(1.0f-t)+val2*t;
	}
	
	private Color interpolate(Color color1, Color color2, float t) {
		float r = interpolate(color1.getR(), color2.getR(), t);
		float g = interpolate(color1.getG(), color2.getG(), t);
		float b = interpolate(color1.getB(), color2.getB(), t);
		float a = interpolate(color1.getA(), color2.getA(), t);
		
		return new Color(r,g,b,a);
	}
	
	public Color getColor(float fraction) {
		float time = fraction * getTotalTime();
		time %= getTotalTime();
		
		float prev_time = 0.0f;
		int size = elements.length;
		
		for (int i = 0; i < size; i++) {
			GradientElement element = elements[i];
			float next_time = prev_time + element.getTotalTime();
			
			if (next_time > time) {
				GradientElement next_element = elements[(i+1) % size];

				float end_fix_time = prev_time + element.getFixation();
				
				if (time >= end_fix_time) {
					float t = (time-end_fix_time) / (next_time-end_fix_time);
					return interpolate(element.getColor(), next_element.getColor(), t);					
				}else {
					return element.getColor();
				}
			}
			prev_time = next_time;
		}
		
		return new Color(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
}

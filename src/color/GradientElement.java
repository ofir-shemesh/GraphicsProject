package color;

public class GradientElement {
	private Color color;
	private float fixation;
	private float interval;
	
	public GradientElement(Color color, float fixation, float interval) {
		this.color = color;
		this.fixation = fixation;
		this.interval = interval;
	}
	
	public float getInterval() {
		return this.interval;
	}
	
	public float getFixation() {
		return this.fixation;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public float getTotalTime() {
		return this.getFixation() + this.getInterval();
	}
}

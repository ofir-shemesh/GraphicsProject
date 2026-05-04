package color;

import utils.MyMath;

/**
 * Represents a color gradient animation.
 * contains a list of colors each being held a certain duration and then
 * linearly interpolated to the next color in a certain duration
 * 
 * The gradient is treated as cyclic, meaning it repeats after completing
 * all elements.
 */
public class ColorGradient {
	GradientElement[] elements;
	
	public ColorGradient(GradientElement[] elements) {
		if (elements == null || elements.length == 0) {
	        throw new IllegalArgumentException("Gradient must have at least one element");
	    }
	    this.elements = elements.clone();
	}
	
	// Gets the total duration of the gradient animation
	
	public float getDuration() {
		float sum = 0.0f;
		
		for (GradientElement element : elements) {
			sum += element.getDuration();
		}

		return sum;
	}
	
	/**
	 * Evaluates the gradient color at a given normalized time.
	 *
	 * Each element has two phases:
	 * - Hold phase: color remains constant
	 * - Transition phase: color interpolates to the next element
	 *
	 * @param fraction - fraction of the total duration used to sample the color
	 * @return interpolated Color at the given time
	 */
	
	public Color getColor(float fraction) {
		// converts from fraction to actual time
		float time = fraction * getDuration();
		time %= getDuration();
		
		// loops through the elements to find the relevant one
		
		float element_start_time = 0.0f;//
		int size = elements.length;
		
		for (int i = 0; i < size; i++) {
			GradientElement element = elements[i];
			float element_finish_time = element_start_time + element.getDuration();
			
			// here we are at the correct element
			if (element_finish_time > time) {
				
				// here we used % size to make the gradient cyclic
				GradientElement next_element = elements[(i+1) % size];
				
				float elment_finish_hold_time = element_start_time + element.getHoldTime();
				
				// inside the transition to the next element
				if (time >= elment_finish_hold_time) {	
					float t = (time-elment_finish_hold_time) / (element_finish_time-elment_finish_hold_time);
					return MyMath.lerp(element.getColor(), next_element.getColor(), t);					
				// inside the hold period of the current element
				}else {
					return element.getColor();
				}
			}
			element_start_time = element_finish_time;
		}
		
		//Should not get here since there should always be at least one element
		throw new IllegalStateException("Gradient evaluation failed");
	}
	
}

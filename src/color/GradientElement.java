package color;

/**
 * Represents a keyframe in a color gradient animation.
 * Each element has a color, a hold time, and a transition time.
 */
public class GradientElement {

    private final Color color;

    // Time the color is held before transition to the next color
    private final float holdTime;
    
    // Time used to interpolate between this and the next color
    private final float transitionTime;

    public GradientElement(Color color, float holdTime, float transitionTime) {
        this.color = color;
        this.holdTime = holdTime;
        this.transitionTime = transitionTime;
    }

    public Color getColor() {
        return color;
    }

    public float getHoldTime() {
        return holdTime;
    }

    public float getTransitionTime() {
        return transitionTime;
    }
    
    //returns total time between this gradient element and the next one
    public float getDuration() {
        return holdTime + transitionTime;
    }
}
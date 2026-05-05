package text;

import color.Color;

/**
 * a Class containing the various parameters defining a style for a text
 * 
 * - Include: thickness, color for fill, stroke
 */

public class TextStyle {
    private float thickness, strokeThickness;
    private Color color, strokeColor;

    public TextStyle(float thickness, float strokeThickness,
                     Color color, Color strokeColor) {
        this.thickness = thickness;
        this.strokeColor = strokeColor;
        this.color = color;
        this.strokeThickness = strokeThickness;
    }

    // --- Getters ---
    public float getThickness() {
        return thickness;
    }

    public float getStrokeThickness() {
        return strokeThickness;
    }

    public Color getColor() {
        return color;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }
}

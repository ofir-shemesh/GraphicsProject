package color;

/**
 * Simple RGBA color container.
 * Stores color components as floats in range [0, 1].
 */

public class Color {

    // Color channels (red, green, blue, alpha)
    private float r, g, b, a;

    /**
     * Creates a new color.
     *
     * @param r red component (0–1)
     * @param g green component (0–1)
     * @param b blue component (0–1)
     * @param a alpha (transparency) component (0–1)
     */
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    // ---- Getters ----

    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
    public float getA() { return a; }

    // ---- Setters ----

    public void setR(float r) { this.r = r; }
    public void setG(float g) { this.g = g; }
    public void setB(float b) { this.b = b; }
    public void setA(float a) { this.a = a; }
}
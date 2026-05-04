package input;

/**
 * a functional interface for running a Runnable taking 4 float parameters
 */
@FunctionalInterface
public interface FloatQuadRunnable {
    void run(float a, float b, float c, float d);
}

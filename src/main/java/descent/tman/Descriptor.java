package descent.tman;

/**
 * Default descriptor comparing distances on a single dimension.
 */
public class Descriptor implements IDescriptor {

	public final double x;

	private static double NUMBER = 0;

	public Descriptor(double x) {
		this.x = x;
	}

	public static IDescriptor get() {
		++Descriptor.NUMBER;
		return new Descriptor(Descriptor.NUMBER);
	}

	public double ranking(IDescriptor other) {
		return Math.abs(((Descriptor) other).x - this.x);
	}

}
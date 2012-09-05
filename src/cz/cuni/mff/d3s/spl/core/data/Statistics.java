package cz.cuni.mff.d3s.spl.core.data;

/**
 * Sample statistics needed to evaluate the SPL formula.
 * 
 * Currently, the interface is very simplified and would be changed in the
 * future.
 * 
 */
public interface Statistics {
	/**
	 * Compute arithmetic mean of the samples.
	 * 
	 * @return Arithmetic mean of the samples at the time of the method call.
	 */
	double getArithmeticMean();
}

package cz.cuni.mff.d3s.spl.core;

/** Enum for possible results of SPL formula. */
public enum SplFormulaResult {
	/** Formula cannot be computed.
	 * 
	 * Typical reason is that there are not enough samples to work with.
	 */
	CANNOT_COMPUTE,
	
	/** Formula holds. */
	COMPLIES,
	
	/** Formula does not hold. */
	VIOLATES
}
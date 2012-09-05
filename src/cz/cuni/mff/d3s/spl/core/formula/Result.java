package cz.cuni.mff.d3s.spl.core.formula;

/** Enum for possible results of SPL formula. */
public enum Result {
	/**
	 * Formula cannot be computed.
	 * 
	 * Typical reason is that there are not enough samples to work with.
	 */
	CANNOT_COMPUTE,

	/** Formula holds. */
	COMPLIES,

	/** Formula does not hold. */
	VIOLATES
}
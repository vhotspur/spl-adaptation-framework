package cz.cuni.mff.d3s.spl.core.formula;

import cz.cuni.mff.d3s.spl.core.data.DataSource;

/** Wrapper for creating service-level-agreement based SPL formulas. */
public class SlaFormula {
	/**
	 * Create SLA formula where measured samples ought to be smaller than given
	 * limit.
	 * 
	 * @param source
	 *            Data source to use.
	 * @param limitNanos
	 *            Limit in nanoseconds.
	 * @return SPL formula representing the SLA.
	 */
	public static Formula createSimple(DataSource source, long limitNanos) {
		return null;
	}
}

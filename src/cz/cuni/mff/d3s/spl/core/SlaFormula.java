package cz.cuni.mff.d3s.spl.core;

import java.util.Collection;

import cz.cuni.mff.d3s.spl.core.datasource.DataSource;

/** SPL formula describing (simple) service level agreement.
 * 
 * The SLA is in the simplest form - i.e. data are smaller than some fixed
 * value.
 */
public class SlaFormula {
	public static final int MIN_SAMPLE_COUNT = 10;
	public static final double SLA_SAFE_MARGIN = 1.1;
	
	private double limit;
	private DataSource source;
	
	/** Create new SPL formula based on SLA.
	 * 
	 * @param source Data source to check SLA with.
	 * @param limit Actual SLA value (max limit for data).
	 */
	public SlaFormula(DataSource source, double limit) {
		this.limit = limit;
		this.source = source;
	}
	
	/** Check that SLA contract is not violated.
	 * 
	 * @return Formula result.
	 */
	public SplFormulaResult checkContract() {
		Collection<Long> data = source.get();
		//System.err.printf("checkContract: %s < %2.2f\n", data, limit);
		if (data.size() < MIN_SAMPLE_COUNT) {
			return SplFormulaResult.CANNOT_COMPUTE;
		}
		
		double actualMean = getSampleMean(data);
		
		if (actualMean * SLA_SAFE_MARGIN > limit) {
			return SplFormulaResult.VIOLATES;
		}
		
		return SplFormulaResult.COMPLIES;
	}
	
	/** Compute arithmetic mean from given list.
	 * 
	 * @param samples List of samples.
	 * @return Arithmetic mean.
	 * @retval 0 When list is empty.
	 */
	protected double getSampleMean(Collection<Long> samples) {
		double sum = 0;
		for (Long l : samples) {
			sum += (double) l;
		}
		return sum / (double) samples.size();
	}
}

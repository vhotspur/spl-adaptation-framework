package cz.cuni.mff.d3s.spl.core.data;

import java.util.Collection;


public class PrecomputedStatistics implements Statistics {
	private double mean = 0.0;
	private long count = 0;

	public static PrecomputedStatistics create(Collection<Long> samples) {
		PrecomputedStatistics result = new PrecomputedStatistics();
		result.mean = getSampleMean(samples);
		result.count = samples.size();
		return result;
	}

	@Override
	public double getArithmeticMean() {
		return mean;
	}

	@Override
	public long getSampleCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return String.format("Statistics(size=%d, mean=%2.2f)",
				count, mean);
	}

	/**
	 * Compute arithmetic mean from given list.
	 * 
	 * @param samples
	 *            List of samples.
	 * @return Arithmetic mean.
	 * @retval 0 When list is empty.
	 */
	protected static double getSampleMean(Collection<Long> samples) {
		if (samples.size() == 0) {
			return 0.0;
		}
		double sum = 0.0;
		for (Long l : samples) {
			sum += (double) l;
		}
		return sum / (double) samples.size();
	}

}

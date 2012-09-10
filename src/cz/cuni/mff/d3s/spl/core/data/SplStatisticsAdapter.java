package cz.cuni.mff.d3s.spl.core.data;

import org.apache.commons.math.stat.descriptive.StatisticalSummary;

public class SplStatisticsAdapter implements StatisticalSummary {
	Statistics backend;
	
	public SplStatisticsAdapter(Statistics adaptee) {
		backend = adaptee;
	}

	@Override
	public double getMax() {
		return 0;
	}

	@Override
	public double getMean() {
		return backend.getArithmeticMean();
	}

	@Override
	public double getMin() {
		return 0;
	}

	@Override
	public long getN() {
		return backend.getSampleCount();
	}

	@Override
	public double getStandardDeviation() {
		return 0;
	}

	@Override
	public double getSum() {
		return getN() * getMean();
	}

	@Override
	public double getVariance() {
		return 0;
	}

}

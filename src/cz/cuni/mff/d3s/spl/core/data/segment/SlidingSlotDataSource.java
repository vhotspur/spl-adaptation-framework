package cz.cuni.mff.d3s.spl.core.data.segment;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class SlidingSlotDataSource implements SampleBasedDataSource {

	private long clockStart = Long.MIN_VALUE;
	private long clockEnd = Long.MAX_VALUE;
	private long sampleCount = 0;
	private double sampleSum = 0.0;
	
	
	public SlidingSlotDataSource(long start, long end) {
		if (start > end) {
			throw new IllegalArgumentException("It is forbidden to have `from > end'.");
		}
		clockStart = start;
		clockEnd = end;
	}

	@Override
	public Statistics get() {
		if (sampleCount == 0) {
			return PrecomputedStatistics.create(Double.NaN, 0);
		}
		return PrecomputedStatistics.create(sampleSum / (double) sampleCount, sampleCount);
	}

	@Override
	public void newSample(long sample, long clock) {
		if ((clock < clockStart) || (clock >= clockEnd)) {
			return;
		}
		
		sampleCount++;
		sampleSum += (double) sample;
	}

	@Override
	public void addSubSource(SampleBasedDataSource subSource) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

}

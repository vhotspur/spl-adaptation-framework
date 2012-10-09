package cz.cuni.mff.d3s.spl.core.data.segment;

import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class SlidingTimeSlotDataSource implements SampleBasedDataSource {
	
	public SlidingTimeSlotDataSource(long start, long end) {
	}
	
	public void shift(long offset) {
		
	}

	@Override
	public Statistics get() {
		return null;
	}

	@Override
	public void newSample(long sample, long clock) {
	}

	@Override
	public void addSubSource(SampleBasedDataSource subSource) {
	}

}

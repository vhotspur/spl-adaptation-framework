package cz.cuni.mff.d3s.spl.core.data.storage;

import java.util.Collection;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class OriginalSerieDataSource implements SerieDataSource {

	private SampleStorage samples;
	private long myStartTime;
	private long myEndTime;

	public OriginalSerieDataSource(SampleStorage storage) {
		this(storage, Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	private OriginalSerieDataSource(SampleStorage storage, long segmentStartTime, long segmentEndTime) {
		samples = storage;
		myStartTime = segmentStartTime;
		myEndTime = segmentEndTime;
	}

	@Override
	public Statistics get() {
		Collection<Long> all = getSamples();
		return PrecomputedStatistics.create(all);		
	}

	@Override
	public SerieDataSource getSegment(long startTime, long endTime) {
		return new OriginalSerieDataSource(samples, startTime, endTime);
	}
	
	@Override
	public String toString() {
		return String.format("SerieDataSource on '%s' (%d)",
				samples, getSamples().size());
	}
	
	private Collection<Long> getSamples() {
		return samples.get(myStartTime, myEndTime).values();
	}
}

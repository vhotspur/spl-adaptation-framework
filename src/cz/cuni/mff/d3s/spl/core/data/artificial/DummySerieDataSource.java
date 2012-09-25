package cz.cuni.mff.d3s.spl.core.data.artificial;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class DummySerieDataSource implements SerieDataSource {
	
	private Statistics stats;
	private static final Long zero = (long) 0;
	private static final List<Long> dummyData = Arrays.asList(zero, zero, zero);
	
	public DummySerieDataSource() {
		stats = PrecomputedStatistics.create(dummyData);
	}

	@Override
	public Statistics get() {
		return stats;
	}

	@Override
	public SerieDataSource getSegment(long startTime, long endTime) {
		return this;
	}
}

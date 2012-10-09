package cz.cuni.mff.d3s.spl.core.data.segment;

import java.util.SortedMap;
import java.util.TreeMap;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class SlidingTimeSlotDataSource implements SampleBasedDataSource {
	private static class TickRecords {
		public long sampleCount = 0;
		public double sampleSum = 0.0;
		
		public TickRecords() {
			
		}
		
		public void newSample(long sample) {
			sampleCount++;
			sampleSum += (double) sample;
		}
	}
	
	private SortedMap<Long, TickRecords> samples = new TreeMap<>();
	private long clockStart = Long.MIN_VALUE;
	private long clockEnd = Long.MAX_VALUE;
	
	public SlidingTimeSlotDataSource(long start, long end) {
		if (start > end) {
			throw new IllegalArgumentException("It is forbidden to have `from > end'.");
		}
		clockStart = start;
		clockEnd = end;
	}
	
	public void shift(long offset) {
		clockStart += offset;
		clockEnd += offset;
		samples = samples.subMap(clockStart, clockEnd);
	}

	@Override
	public Statistics get() {
		double totalSum = 0;
		long sampleCount = 0;
		for (TickRecords rec : samples.values()) {
				totalSum += rec.sampleSum;
				sampleCount += rec.sampleCount;
		}
		if (sampleCount == 0) {
			return PrecomputedStatistics.empty;
		} else {
			return PrecomputedStatistics.create(totalSum / (double) sampleCount, sampleCount);
		}
	}

	@Override
	public void newSample(long sample, long clock) {
		if ((clock < clockStart) || (clock >= clockEnd)) {
			return;
		}
	
		getTickRecord(clock).newSample(sample);
	}

	@Override
	public void addSubSource(SampleBasedDataSource subSource) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	private TickRecords getTickRecord(long clock) {
		TickRecords rec = samples.get(clock);
		if (rec == null) {
			rec = new TickRecords();
			samples.put(clock, rec);
		}
		return rec;
	}
}

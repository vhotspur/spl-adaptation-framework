package cz.cuni.mff.d3s.spl.core.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.SortedMap;

public final class MeasurementPoint implements SampleStorage {
	private static final long MS_TO_NS = 1000 * 1000;
	
	private int skipFactor;
	private int skipCounter;
	private SampleStorage storage;
	private Collection<SampleBasedDataSource> sinks;
	private long milliTimeNanoTimeOffset = 0;
	
	public MeasurementPoint(SampleStorage storage, int howManyRunsToSkip) {
		if (storage == null) {
			throw new IllegalArgumentException("Sample storage can not be null.");
		}
		this.storage = storage;
		skipCounter = 0;
		skipFactor = howManyRunsToSkip;
		sinks = new LinkedList<>();
		milliTimeNanoTimeOffset = System.nanoTime() / MS_TO_NS - System.currentTimeMillis();
	}
	
	public boolean next() {
		skipCounter++;
		if (skipCounter < skipFactor) {
			return false;
		} else {
			skipCounter = 0;
			return true;
		}
	}
	
	public void addSink(SampleBasedDataSource sink) {
		synchronized (sinks) {
			sinks.add(sink);	
		}
	}
	
	@Override
	public void add(long sample, long clock) {
		storage.add(sample, clock);
		synchronized (sinks) {
			for (SampleBasedDataSource sink : sinks) {
				sink.newSample(sample, clock);
			}
		}
	}

	@Override
	public void addFromNanoTimeRange(long startTimeNanos, long endTimeNanos) {
		long diff = endTimeNanos - startTimeNanos;
		long clock = startTimeNanos / MS_TO_NS - milliTimeNanoTimeOffset;
		add(diff, clock);
	}

	@Override
	public SortedMap<Long, Long> get(long startTime, long endTime) {
		return storage.get(startTime, endTime);
	}
}

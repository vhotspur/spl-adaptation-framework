package cz.cuni.mff.d3s.spl.core.data;

import java.util.SortedMap;

public final class MeasurementPoint implements SampleStorage {
	private int skipFactor;
	private int skipCounter;
	private SampleStorage storage;
	
	public MeasurementPoint(SampleStorage storage, int howManyRunsToSkip) {
		if (storage == null) {
			throw new IllegalArgumentException("Sample storage can not be null.");
		}
		this.storage = storage;
		skipCounter = 0;
		skipFactor = howManyRunsToSkip;
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
	
	@Override
	public void add(long sample, long clock) {
		storage.add(sample, clock);
	}

	@Override
	public void addFromNanoTimeRange(long startTimeNanos, long endTimeNanos) {
		storage.addFromNanoTimeRange(startTimeNanos, endTimeNanos);
	}

	@Override
	public SortedMap<Long, Long> get(long startTime, long endTime) {
		return storage.get(startTime, endTime);
	}
}

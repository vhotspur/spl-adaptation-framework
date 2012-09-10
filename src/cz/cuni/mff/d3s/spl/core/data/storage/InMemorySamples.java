package cz.cuni.mff.d3s.spl.core.data.storage;

import java.util.Collections;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import cz.cuni.mff.d3s.spl.core.data.SampleStorage;

/** Stores measured samples in memory. */
public class InMemorySamples implements SampleStorage {

	private static final long MS_TO_NS = 1000 * 1000;
	
	private SortedMap<Long, Long> data = new ConcurrentSkipListMap<>();
	private String id = "";
	private int hash = 0;
	private long milliTimeNanoTimeOffset = 0;
	
	public InMemorySamples(String id) {
		this.id = id;
		this.hash = id.hashCode();
		this.milliTimeNanoTimeOffset = System.nanoTime() / MS_TO_NS - System.currentTimeMillis();
	}

	@Override
	public void add(long sample, long clock) {
		data.put(clock, sample);
	}
	

	@Override
	public void addFromNanoTimeRange(long startTimeNanos, long endTimeNanos) {
		long diff = endTimeNanos - startTimeNanos;
		long clock = startTimeNanos / MS_TO_NS - milliTimeNanoTimeOffset;
		add(diff, clock);
	}

	

	@Override
	public SortedMap<Long, Long> get(long startTime, long endTime) {
		return Collections.unmodifiableSortedMap(data.subMap(startTime, endTime));
	}
	
	@Override
	public String toString() {
		return String.format("Samples[%s](%d)", id, data.size());
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object otherObj) {
		if (this == otherObj) {
			return true;
		}
		
		if (!(otherObj instanceof InMemorySamples)) {
			return false;
		}
		
		InMemorySamples other = (InMemorySamples) otherObj;
		
		return this.id.equals(other.id);
	}

}

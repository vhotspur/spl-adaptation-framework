package cz.cuni.mff.d3s.spl.core.data.storage;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import cz.cuni.mff.d3s.spl.core.data.SampleStorage;

/** Stores measured samples in memory. */
public class InMemorySamples implements SampleStorage {

	private SortedMap<Long, Long> data = new TreeMap<>();
	private String id = "";
	
	public InMemorySamples(String id) {
		this.id = id;
	}

	@Override
	public void add(long sample, long clock) {
		data.put(clock, sample);
	}

	@Override
	public SortedMap<Long, Long> get(long startTime, long endTime) {
		return Collections.unmodifiableSortedMap(data
				.subMap(startTime, endTime));
	}
	
	@Override
	public String toString() {
		return String.format("Samples[%s](%d)", id, data.size());
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

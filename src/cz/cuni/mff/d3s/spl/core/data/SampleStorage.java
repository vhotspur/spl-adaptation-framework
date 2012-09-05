package cz.cuni.mff.d3s.spl.core.data;

import java.util.SortedMap;

/** Backend for storing samples of the performance random variable. */
public interface SampleStorage {
	/**
	 * Add a new sample.
	 * 
	 * @param sample
	 *            Sample (performance) value.
	 * @param clock
	 *            Wall clock time the sample was collected.
	 */
	void add(long sample, long clock);

	/**
	 * Get view on the samples at particular time slot.
	 * 
	 * @param startTime
	 *            Start time (absolute time in milliseconds).
	 * @param endTime
	 *            End time (absolute time in milliseconds).
	 * @return View of the data in pairs (time, sample value).
	 */
	SortedMap<Long, Long> get(long startTime, long endTime);
}

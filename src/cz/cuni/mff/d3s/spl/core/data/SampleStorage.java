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
	 * Add a new sample from a range.
	 * 
	 * The provided times has to be acquired by call to System.nanoTime()
	 * to allow correct conversion to a wall clock time.
	 * 
	 * @param startTimeNanos Nano time the event started.
	 * @param endTimeNanos Nano time the event ended.
	 */
	void addFromNanoTimeRange(long startTimeNanos, long endTimeNanos);

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

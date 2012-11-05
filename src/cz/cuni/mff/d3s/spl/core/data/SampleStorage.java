/*
 * Copyright 2012 Charles University in Prague
 * Copyright 2012 Vojtech Horky
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.cuni.mff.d3s.spl.core.data;

import java.util.SortedMap;

/** Backend for storing samples of the performance random variable. */
@Deprecated
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

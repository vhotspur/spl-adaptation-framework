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

import java.util.Collection;
import java.util.LinkedList;
import java.util.SortedMap;

public final class MeasurementPoint implements SampleStorage {
	private static final long MS_TO_NS = 1000 * 1000;
	
	private int skipFactor = 0;
	private int skipCounter = 0;
	private SampleStorage storage = null;
	private Collection<SampleBasedDataSource> sinks = new LinkedList<>();
	private long milliTimeNanoTimeOffset = 0;
	
	public MeasurementPoint(SampleStorage storage, int howManyRunsToSkip) {
		this(howManyRunsToSkip);
		if (storage == null) {
			throw new IllegalArgumentException("Sample storage can not be null.");
		}
		this.storage = storage;
		
	}
	
	public MeasurementPoint(int howManyRunsToSkip) {
		skipFactor = howManyRunsToSkip;
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
		if (storage != null) {
			storage.add(sample, clock);
		}
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

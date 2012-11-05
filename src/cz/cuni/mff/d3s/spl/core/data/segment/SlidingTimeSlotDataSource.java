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
		/*
		 * We cannot use directly
		 * samples = samples.subMap(clockStart, clockEnd);
		 * because subMap checks that all added values are within
		 * the range and also subMap of a subMap must respect that.
		 * 
		 * Thus, we need to create a whole new Map and copy all the
		 * elements there.
		 */
		SortedMap<Long, TickRecords> newMap = new TreeMap<>();
		newMap.putAll(samples.subMap(clockStart, clockEnd));
		samples = newMap;
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
	
	@Override
	public String toString() {
		String basic = String.format("SlidingTimeSlot<%d,%d)", clockStart, clockEnd);
		StringBuilder result = new StringBuilder(basic);
		return result.toString();
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

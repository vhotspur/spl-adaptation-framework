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

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class FixedTimeSlotDataSource implements SampleBasedDataSource {

	private long clockStart = Long.MIN_VALUE;
	private long clockEnd = Long.MAX_VALUE;
	private long sampleCount = 0;
	private double sampleSum = 0.0;
	
	
	public FixedTimeSlotDataSource(long start, long end) {
		if (start > end) {
			throw new IllegalArgumentException("It is forbidden to have `from > end'.");
		}
		clockStart = start;
		clockEnd = end;
	}

	@Override
	public Statistics get() {
		if (sampleCount == 0) {
			return PrecomputedStatistics.create(Double.NaN, 0);
		}
		return PrecomputedStatistics.create(sampleSum / (double) sampleCount, sampleCount);
	}

	@Override
	public void newSample(long sample, long clock) {
		if ((clock < clockStart) || (clock >= clockEnd)) {
			return;
		}
		
		sampleCount++;
		sampleSum += (double) sample;
	}

	@Override
	public void addSubSource(SampleBasedDataSource subSource) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

}

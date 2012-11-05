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
package cz.cuni.mff.d3s.spl.core.data.storage;

import java.util.Collection;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class OriginalSerieDataSource implements SerieDataSource {

	private SampleStorage samples;
	private long myStartTime;
	private long myEndTime;

	public OriginalSerieDataSource(SampleStorage storage) {
		this(storage, Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	private OriginalSerieDataSource(SampleStorage storage, long segmentStartTime, long segmentEndTime) {
		samples = storage;
		myStartTime = segmentStartTime;
		myEndTime = segmentEndTime;
	}

	@Override
	public Statistics get() {
		Collection<Long> all = getSamples();
		return PrecomputedStatistics.create(all);		
	}

	@Override
	public SerieDataSource getSegment(long startTime, long endTime) {
		return new OriginalSerieDataSource(samples, startTime, endTime);
	}
	
	@Override
	public String toString() {
		return String.format("SerieDataSource on '%s' (%d)",
				samples, getSamples().size());
	}
	
	protected SampleStorage getStorage() {
		return samples;
	}
	
	private Collection<Long> getSamples() {
		return samples.get(myStartTime, myEndTime).values();
	}
}

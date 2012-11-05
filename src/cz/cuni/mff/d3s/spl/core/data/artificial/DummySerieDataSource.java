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
package cz.cuni.mff.d3s.spl.core.data.artificial;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class DummySerieDataSource implements SerieDataSource, SampleBasedDataSource {
	
	private Statistics stats;
	private static final Long zero = (long) 0;
	private static final List<Long> dummyData = Arrays.asList(zero, zero, zero);
	
	public DummySerieDataSource() {
		stats = PrecomputedStatistics.create(dummyData);
	}

	@Override
	public Statistics get() {
		return stats;
	}

	@Override
	public SerieDataSource getSegment(long startTime, long endTime) {
		return this;
	}

	@Override
	public void newSample(long sample, long clock) {
		/* Ignore. */
	}

	@Override
	public void addSubSource(SampleBasedDataSource subSource) {
		/* Ignore. */
	}
}

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

import java.util.Map;
import java.util.SortedMap;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;
import cz.cuni.mff.d3s.spl.core.data.storage.OriginalSerieDataSource;

public class ArtificialSerieDataSource extends OriginalSerieDataSource {

	public ArtificialSerieDataSource(String id) {
		super(Access.getSampleStorage("ARTIFICIAL:" + id));
	}

	private ArtificialSerieDataSource() {
		this("");
	}
	
	public void addSample(long sampleNanos, long clockMillis) {
		getStorage().add(sampleNanos, clockMillis);
	}
	
	public String dumpAllSamples() {
		SortedMap<Long, Long> allSamples = getStorage().get(Long.MIN_VALUE, Long.MAX_VALUE);
		StringBuilder result = new StringBuilder();
		for (Map.Entry<Long, Long> sample : allSamples.entrySet()) {
			result.append(formatSample(sample.getKey(), sample.getValue()));
			result.append(" ");
		}
		return result.toString().trim();
	}
	
	private String formatSample(long clock, long value) {
		return String.format("(%d, %d)", clock, value);
	}
}

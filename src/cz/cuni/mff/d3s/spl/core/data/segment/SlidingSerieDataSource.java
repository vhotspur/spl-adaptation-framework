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

import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

/** Wrapper for creating sliding time slot view on another data source. */
public class SlidingSerieDataSource {

	/**
	 * Create new data source based on existing one as a sliding time slot.
	 * 
	 * The slot (segment) offset specifies a (relative) difference between
	 * current time and end of the window.
	 * 
	 * As an example, the user creates the slot with offset 14 and length 5. And
	 * at time 31 it looks at the data. The samples returned would then
	 * represent data from 12 to 17.
	 * 
	 * @param origin
	 *            Original data source to get data from.
	 * @param slotOffsetSec
	 *            How many seconds to look into the past.
	 * @param slotLengthSec
	 *            Length of the sliding slot in seconds.
	 * @return
	 */
	public static SerieDataSource create(SerieDataSource origin,
			long slotOffsetSec, long slotLengthSec) {
		return new Implementation(origin, slotOffsetSec + slotLengthSec,
				slotOffsetSec);
	}

	private static class Implementation implements SerieDataSource {

		private SerieDataSource origin;
		private long startTimeOffsetMillis;
		private long endTimeOffsetMillis;

		public Implementation(SerieDataSource data, long startOffsetSec,
				long endOffsetSec) {
			origin = data;
			startTimeOffsetMillis = startOffsetSec * 1000;
			endTimeOffsetMillis = endOffsetSec * 1000;
		}

		@Override
		public Statistics get() {
			SerieDataSource segment = getMySegment();
			return segment.get();
		}

		@Override
		public SerieDataSource getSegment(long startTime, long endTime) {
			throw new UnsupportedOperationException();
		}

		private SerieDataSource getMySegment() {
			long now = System.currentTimeMillis();
			return origin.getSegment(now - startTimeOffsetMillis, now - endTimeOffsetMillis);
		}

		@Override
		public String toString() {
			return String.format("Sliding source [%d, %d, %d, %s] above %s",
					startTimeOffsetMillis, endTimeOffsetMillis, System.currentTimeMillis(),
					getMySegment().get(), origin);
		}
	}
}

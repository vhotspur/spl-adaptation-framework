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

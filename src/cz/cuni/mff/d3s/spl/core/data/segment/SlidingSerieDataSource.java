package cz.cuni.mff.d3s.spl.core.data.segment;

import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;

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
		return null;
	}
}

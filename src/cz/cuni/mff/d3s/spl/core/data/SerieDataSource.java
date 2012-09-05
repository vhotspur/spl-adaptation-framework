package cz.cuni.mff.d3s.spl.core.data;

/**
 * Data source that binds individual samples with (wall-clock) time they were
 * obtained.
 */
public interface SerieDataSource extends DataSource {
	/**
	 * Get partial view of the data source at given time segment.
	 * 
	 * @param startTime
	 *            Start time (absolute time in milliseconds).
	 * @param endTime
	 *            End time (absolute time in milliseconds).
	 * @return View of the data as another data source.
	 */
	SerieDataSource getSegment(long startTime, long endTime);
}

package cz.cuni.mff.d3s.spl.core.data;

/** Basic data source representing performance random variable. */
public interface DataSource {
	/**
	 * Get current sample statistics.
	 * 
	 * @return Sample statistics as computed at the time of the method call.
	 */
	Statistics get();
}

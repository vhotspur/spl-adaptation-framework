package cz.cuni.mff.d3s.spl.core.datasource;

import java.util.Collection;

/** SPL data source. */
public interface DataSource {
	/** Get measured times related to this source.
	 * 
	 * @return List of measured times in nanoseconds.
	 */
	Collection<Long> get();
}

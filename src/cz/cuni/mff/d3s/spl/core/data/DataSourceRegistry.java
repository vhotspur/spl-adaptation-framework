package cz.cuni.mff.d3s.spl.core.data;

/** Global register of existing data sources. */
@Deprecated
public interface DataSourceRegistry {
	/**
	 * Retrieve storage for given source id.
	 * 
	 * @param id
	 *            Identification of the data source.
	 * @return Corresponding storage.
	 */
	SampleStorage getStorage(String id);
}

package cz.cuni.mff.d3s.spl.core.datasource;

import java.util.Collection;

import cz.cuni.mff.d3s.spl.core.Measurement;

/** Simplest implementation of SPL data source, returns all data related
 * to the source.
 */
public class SimpleDataSource implements DataSource {
	private String id;
	private Measurement datas;
	
	public SimpleDataSource(String name, Measurement dataStorage) {
		id = name;
		datas = dataStorage;
	}
	
	@Override
	public Collection<Long> get() {
		return datas.get(id);
	}

}

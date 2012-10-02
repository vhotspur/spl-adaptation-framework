package cz.cuni.mff.d3s.spl.core.data.artificial;

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
}

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

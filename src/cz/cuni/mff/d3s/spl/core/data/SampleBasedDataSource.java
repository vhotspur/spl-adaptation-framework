package cz.cuni.mff.d3s.spl.core.data;

public interface SampleBasedDataSource extends DataSource {
	void newSample(long sample, long clock);
	void addSubSource(SampleBasedDataSource subSource);
}

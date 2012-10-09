package cz.cuni.mff.d3s.spl.core.data.storage;

import cz.cuni.mff.d3s.spl.core.data.MeasurementPoint;
import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

public class CompleteDataSource implements SampleBasedDataSource {

	private MeasurementPoint source = null;
	private long sampleCount = 0;
	private double sampleSum = 0.0;
	
	public CompleteDataSource(MeasurementPoint mp) {
		source = mp;
	}
	
	public static CompleteDataSource createFromMeasurementPoint(MeasurementPoint mp) {
		CompleteDataSource source = new CompleteDataSource(mp);
		mp.addSink(source);
		return source;
	}
	
	@Override
	public Statistics get() {
		if (sampleCount == 0) {
			return PrecomputedStatistics.create(Double.NaN, 0);
		}
		return PrecomputedStatistics.create(sampleSum / (double) sampleCount, sampleCount);
	}

	@Override
	public void newSample(long sample, long clock) {
		sampleCount++;
		sampleSum += (double) sample;
		// System.err.printf("newSample(%d, %d) => SUM=%2.2f (%d)\n", sample, clock, sampleSum, sampleCount);
	}

	@Override
	public void addSubSource(SampleBasedDataSource subSource) {
		source.addSink(subSource);
	}

}

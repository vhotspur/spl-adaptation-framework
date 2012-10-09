package cz.cuni.mff.d3s.spl.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.Statistics;
import cz.cuni.mff.d3s.spl.core.data.artificial.ArtificialSerieDataSource;
import static cz.cuni.mff.d3s.spl.test.TestUtils.assertStatisticsEqual;

public class FullSerieDataSourceTest {

	private static double EPSILON = 0.0001;
	private static long MILLIS_TO_SEC = 1000;
	
	private ArtificialSerieDataSource source;
	private Statistics emptySourceStatistics;
	
	@Before
	public void createSource() {
		source = new ArtificialSerieDataSource("junit.test");
		emptySourceStatistics = source.get();
	}
	
	@After
	public void clearCollectedSamples() {
		Access.clearAllSamples();
	}
	
	@Test
	public void emptySourceTest() {
		assertStatisticsEqual(PrecomputedStatistics.empty, emptySourceStatistics, EPSILON);
	}
	
	@Test
	public void singleSampleMeanComputedCorrectly() {
		source.addSample(10, 0);
		assertStatisticsEqual(PrecomputedStatistics.create(10.0, 1), source.get(), EPSILON);
	}
	
	@Test
	public void multipleSamplesMeanComputedCorrectly() {
		source.addSample( 5, 0 * MILLIS_TO_SEC);
		source.addSample( 5, 1 * MILLIS_TO_SEC);
		source.addSample( 6, 2 * MILLIS_TO_SEC);
		source.addSample(10, 3 * MILLIS_TO_SEC);
		
		assertStatisticsEqual(PrecomputedStatistics.create(6.5, 4), source.get(), EPSILON);
	}
}

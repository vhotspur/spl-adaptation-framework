package cz.cuni.mff.d3s.spl.test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;
import cz.cuni.mff.d3s.spl.core.data.artificial.ArtificialSerieDataSource;

public class FullSerieDataSourceTest {

	private static double EPSILON = 0.0001;
	private static long MILLIS_TO_SEC = 1000;
	
	private ArtificialSerieDataSource source;
	private Statistics emptySourceStatistics;
	
	@Before
	public void setUp() {
		source = new ArtificialSerieDataSource("junit.test");
		emptySourceStatistics = source.get();
	}
	
	@Test
	public void emptySourceTest() {
		assertEquals(0, emptySourceStatistics.getSampleCount());
		assertEquals(0.0, emptySourceStatistics.getArithmeticMean(), EPSILON);
	}
	
	@Test
	public void singleSampleMeanComputedCorrectly() {
		source.addSample(10, 0);
		Statistics stats = source.get();
		assertEquals(1, stats.getSampleCount());
		assertEquals(10.0, stats.getArithmeticMean(), EPSILON);
	}
	
	@Test
	public void multipleSamplesMeanComputedCorrectly() {
		source.addSample( 5, 0 * MILLIS_TO_SEC);
		source.addSample( 5, 1 * MILLIS_TO_SEC);
		source.addSample( 6, 2 * MILLIS_TO_SEC);
		source.addSample(10, 3 * MILLIS_TO_SEC);
		Statistics stats = source.get();
		assertEquals(4, stats.getSampleCount());
		assertEquals(6.5, stats.getArithmeticMean(), EPSILON);
	}
}

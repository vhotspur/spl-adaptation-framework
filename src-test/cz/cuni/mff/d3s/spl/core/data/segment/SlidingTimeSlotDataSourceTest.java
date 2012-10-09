package cz.cuni.mff.d3s.spl.core.data.segment;

import static cz.cuni.mff.d3s.spl.test.TestUtils.assertStatisticsEqual;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;

public class SlidingTimeSlotDataSourceTest {

	private final static double EPSILON = 0.0001;
	
	private SlidingTimeSlotDataSource source;
	
	@Before
	public void prepareSamples() {
		source = new SlidingTimeSlotDataSource(0, 10);
		
		for (int i = 0; i < 100; i++) {
			source.newSample(i, i);
		}
	}
	
	@Test
	public void emptySourceProducesEmptyStatistics() {
		SampleBasedDataSource empty = new SlidingTimeSlotDataSource(0, 10);
		assertStatisticsEqual(PrecomputedStatistics.empty, empty.get(), EPSILON);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidIntervalThrowsException() {
		new SlidingTimeSlotDataSource(15, 10);
	}
	
	@Test
	public void testStatisticsComputedCorrectly() {
		/* 0 + 1 + .. + 9 */
		assertStatisticsEqual(PrecomputedStatistics.create(4.5, 10), source.get(), EPSILON);
	}
	
	@Test
	public void statisticsRecomputedAfterShift() {
		source.shift(5);
		/* 5 + 6 + .. + 9 */
		assertStatisticsEqual(PrecomputedStatistics.create(7.0, 5), source.get(), EPSILON);
	}
	
	@Test
	public void shiftCapturesInDifferentInterval() {
		source.shift(5);
		source.newSample(1, 12);
		source.newSample(1, 9999);
		/* 5 + 6 + .. + 9 + 15 */
		assertStatisticsEqual(PrecomputedStatistics.create(6.0, 6), source.get(), EPSILON);
	}
}

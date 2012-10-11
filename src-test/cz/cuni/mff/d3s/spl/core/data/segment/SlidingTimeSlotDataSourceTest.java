package cz.cuni.mff.d3s.spl.core.data.segment;

import static cz.cuni.mff.d3s.spl.test.TestUtils.assertStatisticsEqual;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;

public class SlidingTimeSlotDataSourceTest {

	private final static double EPSILON = 0.0001;
	
	private SlidingTimeSlotDataSource prefilledSource;
	private SlidingTimeSlotDataSource source;
	
	@Before
	public void prepareSamples() {
		prefilledSource = new SlidingTimeSlotDataSource(0, 10);
		
		for (int i = 0; i < 100; i++) {
			prefilledSource.newSample(i, i);
		}
		
		source = new SlidingTimeSlotDataSource(0, 10);
	}
	
	@Test
	public void emptySourceProducesEmptyStatistics() {
		assertStatisticsEqual(PrecomputedStatistics.empty, source.get(), EPSILON);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidIntervalThrowsException() {
		new SlidingTimeSlotDataSource(15, 10);
	}
	
	@Test
	public void testStatisticsComputedCorrectly() {
		/* 0 + 1 + .. + 9 */
		assertStatisticsEqual(PrecomputedStatistics.create(4.5, 10), prefilledSource.get(), EPSILON);
	}
	
	@Test
	public void statisticsRecomputedAfterShift() {
		prefilledSource.shift(5);
		/* 5 + 6 + .. + 9 */
		assertStatisticsEqual(PrecomputedStatistics.create(7.0, 5), prefilledSource.get(), EPSILON);
	}
	
	@Test
	public void shiftCapturesInDifferentInterval() {
		prefilledSource.shift(5);
		prefilledSource.newSample(1, 0);
		prefilledSource.newSample(1, 12);
		prefilledSource.newSample(1, 9999);
		/* 5 + 6 + .. + 9 + 15 */
		assertStatisticsEqual(PrecomputedStatistics.create(6.0, 6), prefilledSource.get(), EPSILON);
	}
	
	@Test
	public void testMultipleShifts() {
		source.newSample(1, 3);
		source.newSample(2, 6);
		source.newSample(3, 9);
		source.newSample(4, 12);
		assertStatisticsEqual(PrecomputedStatistics.create(2.0, 3), source.get(), EPSILON);
		
		/* Will be at <20, 30). */
		source.shift(20);
		assertStatisticsEqual(PrecomputedStatistics.empty, source.get(), EPSILON);
		
		/* Insert some data. */
		source.newSample(100, 15);
		source.newSample(105, 22);
		source.newSample(110, 27);
		source.newSample(120, 42);
		assertStatisticsEqual(PrecomputedStatistics.create(107.5, 2), source.get(), EPSILON);
		
		/* Will be at <35, 45). Empty intersection with previous interval. */
		source.shift(15);
		assertStatisticsEqual(PrecomputedStatistics.empty, source.get(), EPSILON);
		
		source.newSample(163, 39);
		assertStatisticsEqual(PrecomputedStatistics.create(163.0, 1), source.get(), EPSILON);
	}
}

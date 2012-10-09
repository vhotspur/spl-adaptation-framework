package cz.cuni.mff.d3s.spl.core.data.segment;

import org.junit.Test;

import cz.cuni.mff.d3s.spl.core.data.DataSource;
import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.test.TestUtils;

public class SlidingSlotDataSourceTest {

	private final static double EPSILON = 0.0001;

	
	private void assertSourceHasEmptyStatistics(DataSource source) {
		TestUtils.assertStatisticsEqual(source.get(), PrecomputedStatistics.empty, EPSILON);
	}
	
	@Test
	public void emptySourceProducesEmptyStatistics() {
		SlidingSlotDataSource empty = new SlidingSlotDataSource(0, 10);
		assertSourceHasEmptyStatistics(empty);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidInternalThrowsException() {
		new SlidingSlotDataSource(10, 9);
	}
	
	@Test
	public void emptyIntervalSourceRemainsEmpty() {
		SlidingSlotDataSource source = new SlidingSlotDataSource(10, 10);
		assertSourceHasEmptyStatistics(source);
		
		source.newSample(10, 5);
		assertSourceHasEmptyStatistics(source);
		
		source.newSample(20, 10);
		assertSourceHasEmptyStatistics(source);
		
		source.newSample(30, 15);
		assertSourceHasEmptyStatistics(source);
	}
}

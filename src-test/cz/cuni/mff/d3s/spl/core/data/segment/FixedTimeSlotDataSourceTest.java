/*
 * Copyright 2012 Charles University in Prague
 * Copyright 2012 Vojtech Horky
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.cuni.mff.d3s.spl.core.data.segment;

import org.junit.Test;

import cz.cuni.mff.d3s.spl.core.data.DataSource;
import cz.cuni.mff.d3s.spl.core.data.PrecomputedStatistics;
import cz.cuni.mff.d3s.spl.test.TestUtils;

public class FixedTimeSlotDataSourceTest {

	private final static double EPSILON = 0.0001;

	
	private void assertSourceHasEmptyStatistics(DataSource source) {
		TestUtils.assertStatisticsEqual(source.get(), PrecomputedStatistics.empty, EPSILON);
	}
	
	@Test
	public void emptySourceProducesEmptyStatistics() {
		FixedTimeSlotDataSource empty = new FixedTimeSlotDataSource(0, 10);
		assertSourceHasEmptyStatistics(empty);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invalidIntervalThrowsException() {
		new FixedTimeSlotDataSource(10, 9);
	}
	
	@Test
	public void emptyIntervalSourceRemainsEmpty() {
		FixedTimeSlotDataSource source = new FixedTimeSlotDataSource(10, 10);
		assertSourceHasEmptyStatistics(source);
		
		source.newSample(10, 5);
		assertSourceHasEmptyStatistics(source);
		
		source.newSample(20, 10);
		assertSourceHasEmptyStatistics(source);
		
		source.newSample(30, 15);
		assertSourceHasEmptyStatistics(source);
	}
}

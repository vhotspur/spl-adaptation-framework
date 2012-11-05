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
package cz.cuni.mff.d3s.spl.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.core.data.DataSource;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;
import cz.cuni.mff.d3s.spl.core.data.artificial.ArtificialSerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.segment.SlidingSerieDataSource;

public class SilidingSerieDataSourceTest {
	private static final double EPSILON = 0.0001;
	private static final long SEC_TO_MILLIS = 1000;
	private static final long MILLIS_TO_NANOS = 1000 * 1000;
	
	private static long suiteStartTimeMillis = 0;
	private static long suiteStartTimeNanos = 0;
	
	private ArtificialSerieDataSource source;
	
	@MockClass(realClass = System.class)
	public static class SystemMock {
		public static long mockClockMillis = 0;
		
		@Mock
		public static long currentTimeMillis() {
			return mockClockMillis;
		}
	}
	
	@BeforeClass
	public static void initTimeSources() {
		suiteStartTimeMillis = System.currentTimeMillis();
		suiteStartTimeNanos = System.nanoTime();
	}
	
	@Before
	public void prepareSource() {
		source = new ArtificialSerieDataSource("junit.test.sliding");
		/* Add sample where position in time is sample value. */
		for (int i = 0; i <= 100; i++) {
			source.addSample(i, i * SEC_TO_MILLIS);
		}		
	}
	
	@Before
	public void mockSystemClass() {
		Mockit.setUpMock(SystemMock.class);
	}
	
	@After
	public void pseudoSyncMockedClock() {
		long currentTimeNanos = System.nanoTime();
		long diffNanos = currentTimeNanos - suiteStartTimeNanos;
		long diffMillis = diffNanos / MILLIS_TO_NANOS;
		long predictedClock = suiteStartTimeMillis + diffMillis; 
		SystemMock.mockClockMillis = predictedClock;
	}
	
	@After
	public void clearCollectedSamples() {
		Access.clearAllSamples();
	}
	
	private void assertMockClock(long expectedTime) {
		long actualCurrentTime = System.currentTimeMillis();
		assertEquals(expectedTime, actualCurrentTime);
	}
	
	private void setMockedClock(long newCurrentTimeMillis) {
		SystemMock.mockClockMillis = newCurrentTimeMillis;
		assertMockClock(newCurrentTimeMillis);
	}
	
	@Test
	public void mockingCurrentTimeMillisWorks() {
		setMockedClock(0);
		setMockedClock(1000);
		setMockedClock(12345);
	}
	
	private void checkSourceStats(DataSource dataSource,
			int expectedSampleCount, double expectedMean) {
		Statistics stats = dataSource.get();
		assertEquals(expectedSampleCount, stats.getSampleCount());
		assertEquals(expectedMean, stats.getArithmeticMean(), EPSILON);
	}
	
	@Test
	public void slidingSourceOnTheWholeData() {
		setMockedClock(100 * SEC_TO_MILLIS + 1);
		SerieDataSource allData = SlidingSerieDataSource.create(source, 0, 100);
		checkSourceStats(allData, 100, 50.5);
		assertMockClock(100 * SEC_TO_MILLIS + 1);
	}
	
	@Test
	public void slidingSourceLastSeconds() {
		setMockedClock(100 * SEC_TO_MILLIS + 1);
		SerieDataSource allData = SlidingSerieDataSource.create(source, 0, 5);
		checkSourceStats(allData, 5, 98.0);
		assertMockClock(100 * SEC_TO_MILLIS + 1);
	}
	
	@Test
	public void slidingSourceInTheMiddleOfTheData() {
		setMockedClock(100 * SEC_TO_MILLIS + 1);
		SerieDataSource allData = SlidingSerieDataSource.create(source, 60, 10);
		checkSourceStats(allData, 10, 35.5);
		assertMockClock(100 * SEC_TO_MILLIS + 1);
	}
}

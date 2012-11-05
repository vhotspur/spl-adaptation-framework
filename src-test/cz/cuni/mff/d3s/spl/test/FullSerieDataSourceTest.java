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

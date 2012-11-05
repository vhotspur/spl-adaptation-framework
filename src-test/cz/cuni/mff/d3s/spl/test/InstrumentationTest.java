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

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.agent.Settings;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import cz.cuni.mff.d3s.spl.core.data.storage.CompleteDataSource;

public class InstrumentationTest {

	private static final String INSTRUMENTED_CLASS = "cz.cuni.mff.d3s.spl.test.TestClass";
	private static final String INSTRUMENTED_METHOD = "action";
	
	private static SerieDataSource instrumentationSource;
	private static CompleteDataSource source;
	
	@BeforeClass
	public static void instrumentAction() {
		Settings.DEFAULT_SKIP_FACTOR = 0;
		instrumentationSource = InstrumentingDataSource.create(INSTRUMENTED_CLASS, INSTRUMENTED_METHOD);
		source = CompleteDataSource.createFromMeasurementPoint(Access.getMeasurementPoint(InstrumentingDataSource.createId(INSTRUMENTED_CLASS, INSTRUMENTED_METHOD)));
		
		runInstrumentedMethod(1234);
	}
	
	@Test
	public void allRunsAreRecordedCorrectly() {
		assertEquals(1234, instrumentationSource.get().getSampleCount());
	}
	
	@Test
	public void completeDataSourceRegisteredAllRuns() {
		assertEquals(1234, source.get().getSampleCount());
	}
	
	private static void runInstrumentedMethod(int count) {
		while (count > 0) {
			TestClass.action();
			count--;
		}
	}
}

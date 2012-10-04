package cz.cuni.mff.d3s.spl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.agent.Settings;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;

public class InstrumentationTest {

	private static final String INSTRUMENTED_CLASS = "cz.cuni.mff.d3s.spl.test.TestClass";
	private static final String INSTRUMENTED_METHOD = "action";
	
	private static SerieDataSource instrumentationSource;
	
	@BeforeClass
	public static void instrumentAction() {
		Settings.DEFAULT_SKIP_FACTOR = 0;
		instrumentationSource = InstrumentingDataSource.create(INSTRUMENTED_CLASS, INSTRUMENTED_METHOD);
	}
	
	@Test
	public void allRunsAreRecordedCorrectly() {
		runInstrumentedMethod(1234);
		
		long sampleCount = instrumentationSource.get().getSampleCount();
		assertEquals(1234, sampleCount);
	}
	
	private void runInstrumentedMethod(int count) {
		while (count > 0) {
			TestClass.action();
			count--;
		}
	}
}

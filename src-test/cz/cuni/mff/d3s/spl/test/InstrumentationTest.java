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

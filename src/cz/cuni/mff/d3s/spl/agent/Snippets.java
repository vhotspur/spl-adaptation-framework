package cz.cuni.mff.d3s.spl.agent;

import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.SyntheticLocal;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;

public class Snippets {
	@SyntheticLocal
	private static final int SKIP_COUNT = 100;
	
	@SyntheticLocal
	private static long startTime = 0;
	
	/** Whether to skip current measurement. */
	@SyntheticLocal
	private static boolean skip = false;

	@Before(marker = BodyMarker.class, guard = Access.class)
	public static void startMeasuring() {
		Access.counter++;
		if (Access.counter < SKIP_COUNT) {
			skip = true;
			return;
		} else {
			Access.counter = 0;
		}
		startTime = System.nanoTime();
	}

	@After(marker = BodyMarker.class, guard = Access.class)
	public static void endMeasureAnnounceResults(MethodStaticContext sc) {
		if (skip) {
			skip = false;
			return;
		}
		long now = System.nanoTime();
		
		String id = InstrumentingDataSource.createId(sc.thisClassName(), sc.thisMethodName());
		SampleStorage storage = Access.getSampleStorage(id);
		storage.addFromNanoTimeRange(startTime, now);
	}
}

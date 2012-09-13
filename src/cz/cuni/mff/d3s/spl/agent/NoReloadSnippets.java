package cz.cuni.mff.d3s.spl.agent;

import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.SyntheticLocal;
import ch.usi.dag.disl.annotation.SyntheticStaticField;
import ch.usi.dag.disl.annotation.SyntheticStaticField.Scope;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;

public class NoReloadSnippets {
	@SyntheticLocal
	private static final int SKIP_COUNT = 10;
	
	@SyntheticLocal
	private static long startTime = 0;
	
	/** Whether to skip current measurement. */
	@SyntheticLocal
	private static boolean skip = false;
	
	@SyntheticStaticField(scope=Scope.PERCLASS)
	public static boolean measureThisClass = false;
	
	@SyntheticStaticField(scope=Scope.PERMETHOD)
	private static int methodSkipCounter = 0;

	@Before(marker = BodyMarker.class, guard = Access.class)
	public static void startMeasuring() {
		/*
		 * If we are not supposed to measure this method, just bail
		 * out immediately.
		 * 
		 */
		if (!measureThisClass) {
			return;
		}
		
		/*
		 * See if this run shall be measured.
		 */
		// FIXME: randomize, this is way to predictable
		methodSkipCounter++;
		if (methodSkipCounter < SKIP_COUNT) {
			skip = true;
			return;
		}
		
		/*
		 * Start the measuring.
		 * Also, we need to reset the counter.
		 */
		methodSkipCounter = 0;
		startTime = System.nanoTime();
	}

	@After(marker = BodyMarker.class, guard = Access.class)
	public static void endMeasureAnnounceResults(MethodStaticContext sc) {
		/*
		 * If this run is not measure or we do not measure at all,
		 * we return immediately.
		 */
		if (!measureThisClass) {
			return;
		}
		if (skip) {
			skip = false;
			return;
		}
		
		/*
		 * Finish the measuring.
		 */
		long now = System.nanoTime();
		
		// TODO: cache the storage in method static local
		String id = InstrumentingDataSource.createId(sc.thisClassName(), sc.thisMethodName());
		SampleStorage storage = Access.getSampleStorage(id);
		storage.addFromNanoTimeRange(startTime, now);
	}
}

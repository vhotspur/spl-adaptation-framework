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
	private static long startTime = 0;

	@Before(marker = BodyMarker.class, guard = Access.class)
	public static void startMeasuring() {
		startTime = System.nanoTime();
	}

	@After(marker = BodyMarker.class, guard = Access.class)
	public static void endMeasureAnnounceResults(MethodStaticContext sc) {
		try {
			long now = System.nanoTime();
			long runLengthNanos = (now - startTime);
			long nowMillis = System.currentTimeMillis();
			
			// String probeName = sc.thisMethodFullName().replace('.', '#').replace('/', '.');
			// System.err.printf("%s runs for %dns (started at %dms since epoch).\n", probeName, runLengthNanos, nowMillis);
			
			SampleStorage storage = Access.getSampleStorage(InstrumentingDataSource.createId(sc.thisClassName(), sc.thisMethodName()));
			storage.add(runLengthNanos, nowMillis);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

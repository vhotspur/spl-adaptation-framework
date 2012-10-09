package cz.cuni.mff.d3s.spl.example.newton.checker;

import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;
import cz.cuni.mff.d3s.spl.core.data.artificial.DummySerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import cz.cuni.mff.d3s.spl.core.formula.Formula;
import cz.cuni.mff.d3s.spl.core.formula.Result;
import cz.cuni.mff.d3s.spl.core.formula.SlaFormula;

public class SlaChecker implements Runnable {

	private final static String CLASS = "org.apache.commons.math.analysis.solvers.NewtonSolver";
	private final static String METHOD = "solve";
	private final static long SEC_TO_NANOS = 1000 * 1000 * 1000;

	SampleBasedDataSource source;
	Formula sla;
	
	public SlaChecker() {
		init();
	}
	
	public SlaChecker(String args) {
		if (args.equals("no-measuring")) {
			init(new DummySerieDataSource());
		} else {
			init();
		}
	}
	
	private void init() {
		init(InstrumentingDataSource.createSampleBased(CLASS, METHOD));
	}
	
	private void init(SampleBasedDataSource originalSource) {
		source = originalSource;
		sla = SlaFormula.createSimple(source, 1 * SEC_TO_NANOS);
	}

	@Override
	public void run() {
		Result result = sla.evaluate();
		Statistics stats = source.get();

		/* Explicitly flush all previous output to prevent interleaving. */
		System.out.flush();
		System.err.flush();
		
		System.out.printf(
				"NewtonSolver.solve(): mean is %2.1fns, %d samples: %s.\n",
				stats.getArithmeticMean(), stats.getSampleCount(), result);
	}
}

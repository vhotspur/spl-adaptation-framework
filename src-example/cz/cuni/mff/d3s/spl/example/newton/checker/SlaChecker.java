package cz.cuni.mff.d3s.spl.example.newton.checker;

import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;
import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import cz.cuni.mff.d3s.spl.core.formula.Formula;
import cz.cuni.mff.d3s.spl.core.formula.Result;
import cz.cuni.mff.d3s.spl.core.formula.SlaFormula;

public class SlaChecker implements Runnable {

	private final static String METHOD = "org.apache.commons.math.analysis.solvers.NewtonSolver#solve";
	private final static long SEC_TO_NANOS = 1000 * 1000 * 1000;

	SerieDataSource source;
	Formula sla;
	
	public SlaChecker() {
		source = InstrumentingDataSource.create(METHOD);
		sla = SlaFormula.createSimple(source, 1 * SEC_TO_NANOS);
	}

	@Override
	public void run() {
		Result result = sla.evaluate();
		Statistics stats = source.get();

		/* Explicitly flush all previous output to prevent interleaving. */
		System.out.flush();
		System.err.flush();
		
		System.err.printf(
				"NewtonSolver.solve(): mean is %2.1fns, %d samples: %s.\n",
				stats.getArithmeticMean(), stats.getSampleCount(), result);
	}
}

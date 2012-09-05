package cz.cuni.mff.d3s.spl.example.checksla.checker;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import cz.cuni.mff.d3s.spl.core.data.segment.SlidingSerieDataSource;
import cz.cuni.mff.d3s.spl.core.formula.Formula;
import cz.cuni.mff.d3s.spl.core.formula.Result;
import cz.cuni.mff.d3s.spl.core.formula.SlaFormula;

public class SlaChecker implements Runnable {

	private final static String SHORT_METHOD = "cz.cuni.mff.d3s.spl.example.checksla.app.Main#shortMethod";
	private final static String LONG_METHOD = "cz.cuni.mff.d3s.spl.example.checksla.app.Main#longMethod";
	private final static long SEC_TO_NANOS = 1000 * 1000 * 1000;
	private final static long MILLIS_TO_NANOS = 1000 * 1000;

	private Map<String, Formula> formulas;

	public SlaChecker() {
		formulas = new HashMap<>();
	}

	@Override
	public void run() {
		sleepSec(3);

		init();

		System.err.printf("SlaChecker started!\n");

		while (true) {
			for (Map.Entry<String, Formula> f : formulas.entrySet()) {
				checkFormula(f.getValue(), f.getKey());
			}

			sleepSec(2);
		}
	}

	protected void sleepSec(long sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException ignored) {
		}
	}

	protected void checkFormula(Formula formula, String name) {
		Result result = formula.evaluate();

		// System.err.printf("Checked contract of %s: %s.\n", name, result);

		if (result == Result.VIOLATES) {
			System.err.printf("Contract of `%s' violated!\n", name);
		}
	}

	private void init() {
		formulas.put("short method", SlaFormula.createSimple(
				SlidingSerieDataSource.create(
						InstrumentingDataSource.create(SHORT_METHOD), 0, 5),
				1 * SEC_TO_NANOS));
		formulas.put("long method", SlaFormula.createSimple(
				SlidingSerieDataSource.create(
						InstrumentingDataSource.create(LONG_METHOD), 0, 5),
				1 * MILLIS_TO_NANOS));
	}

}

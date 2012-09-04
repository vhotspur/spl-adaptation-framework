package cz.cuni.mff.d3s.spl.example.checksla.checker;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.core.Measurement;
import cz.cuni.mff.d3s.spl.core.SlaFormula;
import cz.cuni.mff.d3s.spl.core.SplFormulaResult;
import cz.cuni.mff.d3s.spl.core.datasource.SlidingTimeSlotDataSource;

public class SlaChecker implements Runnable {

	private final static String SHORT_METHOD = "cz.cuni.mff.d3s.spl.example.checksla.app.Main#shortMethod";
	private final static String LONG_METHOD = "cz.cuni.mff.d3s.spl.example.checksla.app.Main#longMethod";
	private final static long SEC_TO_NANOS = 1000 * 1000 * 1000;
	private final static long MILLIS_TO_NANOS = 1000 * 1000;
	
	private Map<String, SlaFormula> formulas;

	public SlaChecker() {
		Measurement datas = Access.getMeasurement();
		
		formulas = new HashMap<String, SlaFormula>();
		formulas.put("short method", new SlaFormula(SlidingTimeSlotDataSource.createSlotSeconds(
				SHORT_METHOD, datas, 0, 5), 1 * SEC_TO_NANOS));
		formulas.put("long method", new SlaFormula(SlidingTimeSlotDataSource.createSlotSeconds(
				LONG_METHOD, datas, 0, 5), 1 * MILLIS_TO_NANOS));
	}
				
				

	@Override
	public void run() {	
		sleepSec(3);
		
		System.err.printf("SlaChecker started!\n");
		
		Access.instrument(SHORT_METHOD);
		Access.instrument(LONG_METHOD);
		
		while (true) {		
			for (Map.Entry<String, SlaFormula> formulaEntry : formulas.entrySet()) {
				SlaFormula formula = formulaEntry.getValue();
				String name = formulaEntry.getKey();
				SplFormulaResult slaCompliance = formula.checkContract();
				
				// System.err.printf("Checked contract of %s: %s.\n", name, slaCompliance);
				
				if (slaCompliance == SplFormulaResult.VIOLATES) {
					System.err.printf("Contract of `%s' violated!\n", name);
				}				
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
}

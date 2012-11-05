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
package cz.cuni.mff.d3s.spl.example.checksla.checker;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;
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
		Access.getMeasurementPoint(InstrumentingDataSource.createId(SHORT_METHOD)).addSink(new ReportWhenMeasured());
	}

	private static class ReportWhenMeasured implements SampleBasedDataSource {
		@Override
		public Statistics get() {
			throw new UnsupportedOperationException(
					"Don't know statistics or any mathematics at all, sorry.");
		}

		@Override
		public void newSample(long sample, long clock) {
			System.err.printf("Collected sample %d at time %d.\n", sample, clock);
		}

		@Override
		public void addSubSource(SampleBasedDataSource subSource) {
			throw new UnsupportedOperationException("This is not implemented.");
		}
		
	}
}

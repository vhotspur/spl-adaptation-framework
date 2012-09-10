package cz.cuni.mff.d3s.spl.core.formula;

import java.util.NoSuchElementException;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.stat.inference.TTest;
import org.apache.commons.math.stat.inference.TTestImpl;

import cz.cuni.mff.d3s.spl.core.data.DataSource;
import cz.cuni.mff.d3s.spl.core.data.SplStatisticsAdapter;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

/** Wrapper for creating service-level-agreement based SPL formulas. */
public class SlaFormula {
	public static final int MIN_SAMPLE_COUNT = 10;
	public static final double T_TEST_SIGNIFICANCE_LEVEL = 0.95;
	
	/**
	 * Create SLA formula where measured samples ought to be smaller than given
	 * limit.
	 * 
	 * @param source
	 *            Data source to use.
	 * @param limitNanos
	 *            Limit in nanoseconds.
	 * @return SPL formula representing the SLA.
	 */
	public static Formula createSimple(DataSource source, long limitNanos) {
		return new SimpleSla(source, limitNanos);
	}

	private static class SimpleSla implements Formula {

		private DataSource source;
		private long limitNanos;
		private TTest test;

		public SimpleSla(DataSource datasource, long limitNanosec) {
			source = datasource;
			limitNanos = limitNanosec;
			test = new TTestImpl();
		}

		@Override
		public void bind(String variable, DataSource data)
				throws NoSuchElementException {
			throw new UnsupportedOperationException(
					"It is not possible to bind source to simple SLA formula.");
		}

		@Override
		public Result evaluate() {
			Statistics stats = source.get();
			
			System.err.printf("Have %d samples.\n", stats.getSampleCount());
			
			if (stats.getSampleCount() < MIN_SAMPLE_COUNT) {
				return Result.CANNOT_COMPUTE;
			}
			
			StatisticalSummary testStats = new SplStatisticsAdapter(stats);
			
			boolean slaOkay = isMeanSmaller(testStats);
			if (slaOkay) {
				return Result.COMPLIES;
			} else {
				return Result.VIOLATES;
			}
		}
		
		private boolean isMeanSmaller(StatisticalSummary stats) {
			double alpha = 1.0 - T_TEST_SIGNIFICANCE_LEVEL;
			try {
				/*
				 * We are running one-sided test and thus we need to
				 * multiply alpha by 2.
				 * See example at TTest.tTest.
				 */
				return test.tTest(limitNanos, stats, alpha * 2.);
			} catch (IllegalArgumentException e) {
				/* This shall not happen. */
				e.printStackTrace();
				return true;
			} catch (MathException e) {
				/* Very unlikely to happen. */
				e.printStackTrace();
				return false;
			}
		}
	}
}

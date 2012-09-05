package cz.cuni.mff.d3s.spl.core.formula;

import java.util.NoSuchElementException;

import cz.cuni.mff.d3s.spl.core.data.DataSource;
import cz.cuni.mff.d3s.spl.core.data.Statistics;

/** Wrapper for creating service-level-agreement based SPL formulas. */
public class SlaFormula {
	public static final int MIN_SAMPLE_COUNT = 10;
	
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

		public SimpleSla(DataSource datasource, long limitNanosec) {
			source = datasource;
			limitNanos = limitNanosec;
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
			
			if (stats.getSampleCount() < MIN_SAMPLE_COUNT) {
				return Result.CANNOT_COMPUTE;
			}
			
			if (stats.getArithmeticMean() * 1.1 > limitNanos) {
				return Result.VIOLATES;
			} else {
				return Result.COMPLIES;
			}
		}
	}
}

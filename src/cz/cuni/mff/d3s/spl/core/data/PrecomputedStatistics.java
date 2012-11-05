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
package cz.cuni.mff.d3s.spl.core.data;

import java.io.Serializable;
import java.util.Collection;


public class PrecomputedStatistics implements Statistics, Serializable {
	private static final long serialVersionUID = -8047285832373708558L;
	
	private double mean = 0.0;
	private long count = 0;
	
	public static final PrecomputedStatistics empty = PrecomputedStatistics.create(Double.NaN, 0);

	public static PrecomputedStatistics create(Collection<Long> samples) {
		PrecomputedStatistics result = new PrecomputedStatistics();
		result.mean = getSampleMean(samples);
		result.count = samples.size();
		return result;
	}
	
	public static PrecomputedStatistics create(double mean, long count) {
		PrecomputedStatistics result = new PrecomputedStatistics();
		result.mean = mean;
		result.count = count;
		return result;
	}

	@Override
	public double getArithmeticMean() {
		return mean;
	}

	@Override
	public long getSampleCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return String.format("Statistics(size=%d, mean=%2.2f)",
				count, mean);
	}

	/**
	 * Compute arithmetic mean from given list.
	 * 
	 * @param samples
	 *            List of samples.
	 * @return Arithmetic mean.
	 * @retval 0 When list is empty.
	 */
	protected static double getSampleMean(Collection<Long> samples) {
		if (samples.size() == 0) {
			return Double.NaN;
		}
		double sum = 0.0;
		for (Long l : samples) {
			sum += (double) l;
		}
		return sum / (double) samples.size();
	}

}

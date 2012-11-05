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

import org.apache.commons.math.stat.descriptive.StatisticalSummary;

public class SplStatisticsAdapter implements StatisticalSummary {
	Statistics backend;
	
	public SplStatisticsAdapter(Statistics adaptee) {
		backend = adaptee;
	}

	@Override
	public double getMax() {
		return 0;
	}

	@Override
	public double getMean() {
		return backend.getArithmeticMean();
	}

	@Override
	public double getMin() {
		return 0;
	}

	@Override
	public long getN() {
		return backend.getSampleCount();
	}

	@Override
	public double getStandardDeviation() {
		return 0;
	}

	@Override
	public double getSum() {
		return getN() * getMean();
	}

	@Override
	public double getVariance() {
		return 0;
	}

}

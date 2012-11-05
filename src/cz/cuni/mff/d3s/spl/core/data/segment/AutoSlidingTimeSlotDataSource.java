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
package cz.cuni.mff.d3s.spl.core.data.segment;

import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;

public class AutoSlidingTimeSlotDataSource extends SlidingTimeSlotDataSource {
	private static final long SEC_TO_MILLIS = 1000;
	private long lastClock = 0;
	
	public static AutoSlidingTimeSlotDataSource createSec(long slotOffsetSec, long slotLengthSec) {
		long startTimeSec = slotOffsetSec - slotLengthSec;
		long startTimeMillis = startTimeSec * SEC_TO_MILLIS;
		long endTimeMillis = slotOffsetSec * SEC_TO_MILLIS;
		return new AutoSlidingTimeSlotDataSource(startTimeMillis, endTimeMillis);
	}
	
	public static AutoSlidingTimeSlotDataSource createSec(SampleBasedDataSource parent, long slotOffsetSec, long slotLengthSec) {
		AutoSlidingTimeSlotDataSource result = createSec(slotOffsetSec, slotLengthSec);
		parent.addSubSource(result);
		return result;
	}
	
	private AutoSlidingTimeSlotDataSource() {
		this(0, 0);
	}
	
	private AutoSlidingTimeSlotDataSource(long start, long end) {
		super(start, end);
	}
	
	@Override
	public void shift(long offset) {
		throw new UnsupportedOperationException("shift() does not make sense auto-sliding source.");
	}
	
	@Override
	public void newSample(long sample, long clock) {
		long clockNow = System.currentTimeMillis() + 1;
		super.shift(clockNow - lastClock);
		lastClock = clockNow;
		super.newSample(sample, clock);
	}
}

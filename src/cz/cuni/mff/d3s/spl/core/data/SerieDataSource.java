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

/**
 * Data source that binds individual samples with (wall-clock) time they were
 * obtained.
 */
@Deprecated
public interface SerieDataSource extends DataSource {
	/**
	 * Get partial view of the data source at given time segment.
	 * 
	 * @param startTime
	 *            Start time (absolute time in milliseconds).
	 * @param endTime
	 *            End time (absolute time in milliseconds).
	 * @return View of the data as another data source.
	 */
	SerieDataSource getSegment(long startTime, long endTime);
}

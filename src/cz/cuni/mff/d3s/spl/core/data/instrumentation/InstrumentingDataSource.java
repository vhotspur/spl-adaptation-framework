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
package cz.cuni.mff.d3s.spl.core.data.instrumentation;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.core.data.MeasurementPoint;
import cz.cuni.mff.d3s.spl.core.data.SampleBasedDataSource;
import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.storage.CompleteDataSource;
import cz.cuni.mff.d3s.spl.core.data.storage.OriginalSerieDataSource;

/**
 * Wrapper for creating data source from automatically instrumented methods.
 */
public class InstrumentingDataSource {

	public static SampleBasedDataSource createSampleBased(String classname, String methodname) {
		classname = classname.replace('/', '.');
		String id = createId(classname, methodname);
		MeasurementPoint point = Access.getMeasurementPoint(id);
		Access.instrumentMethod(classname, methodname);
		return CompleteDataSource.createFromMeasurementPoint(point);
	}
	
	public static SampleBasedDataSource createSampleBased(String fullMethodName) {
		String parts[] = fullMethodName.split("#", 2);
		if (parts.length != 2) {
			throw new IllegalArgumentException(String.format(
					"%s is not a valid method specification.", fullMethodName));
		}
		return createSampleBased(parts[0], parts[1]);
	}
	
	/**
	 * Create data source with automatic instrumentation.
	 * 
	 * @param classname
	 *            Full class name (dot or slash separated packages).
	 * @param methodname
	 *            Bare method name (without parameter specification).
	 * @return Data source representing given method performance.
	 */
	@Deprecated
	public static SerieDataSource create(String classname, String methodname) {
		classname = classname.replace('/', '.');
		String id = createId(classname, methodname);
		SampleStorage storage = Access.getSampleStorage(id);
		Access.instrumentMethod(classname, methodname);
		return new OriginalSerieDataSource(storage);
	}

	/**
	 * Create data source with automatic instrumentation.
	 * 
	 * The method name consists of package specification (use dots to separate
	 * them), followed by class name (separated by dot from packages), followed
	 * by hash sign (#) and method name. Do not specify any argument types (i.e.
	 * no way to distinguish overloaded methods).
	 * 
	 * @param fullMethodName
	 *            Full method name (packages + class + method).
	 * @return Data source representing given method performance.
	 */
	@Deprecated
	public static SerieDataSource create(String fullMethodName) {
		String parts[] = fullMethodName.split("#", 2);
		if (parts.length != 2) {
			throw new IllegalArgumentException(String.format(
					"%s is not a valid method specification.", fullMethodName));
		}
		return create(parts[0], parts[1]);
	}

	public static String createId(String classname, String methodname) {
		return "INSTRUMENT:" + classname.replace('/', '.') + "#" + methodname;
	}
	
	public static String createId(String fullMethodName) {
		String parts[] = fullMethodName.split("#", 2);
		if (parts.length != 2) {
			throw new IllegalArgumentException(String.format(
					"%s is not a valid method specification.", fullMethodName));
		}
		return createId(parts[0], parts[1]);
	}
}

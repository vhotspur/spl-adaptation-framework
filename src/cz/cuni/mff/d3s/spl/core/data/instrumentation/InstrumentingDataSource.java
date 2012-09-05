package cz.cuni.mff.d3s.spl.core.data.instrumentation;

import cz.cuni.mff.d3s.spl.agent.Access;
import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;
import cz.cuni.mff.d3s.spl.core.data.storage.OriginalSerieDataSource;

/**
 * Wrapper for creating data source from automatically instrumented methods.
 */
public class InstrumentingDataSource {

	/**
	 * Create data source with automatic instrumentation.
	 * 
	 * @param classname
	 *            Full class name (dot or slash separated packages).
	 * @param methodname
	 *            Bare method name (without parameter specification).
	 * @return Data source representing given method performance.
	 */
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
	public static SerieDataSource create(String fullMethodName) {
		String parts[] = fullMethodName.split("#", 2);
		if (parts.length != 2) {
			throw new IllegalArgumentException(String.format(
					"%s is not a valid method specification.", fullMethodName));
		}
		return create(parts[0], parts[1]);
	}

	public static String createId(String classname, String methodname) {
		return String.format("INSTRUMENT:%s#%s", classname.replace('/', '.'),
				methodname);
	}
}

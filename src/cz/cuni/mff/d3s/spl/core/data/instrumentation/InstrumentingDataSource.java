package cz.cuni.mff.d3s.spl.core.data.instrumentation;

import cz.cuni.mff.d3s.spl.core.data.SerieDataSource;

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
		return null;
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
		return null;
	}
}

package cz.cuni.mff.d3s.spl.agent;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.spl.core.data.MeasurementPoint;
import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.storage.InMemorySamples;

/** Wrapper for accessing instrumentation agent and measurement results. */
public class Access {
	/** Instrument given method.
	 * 
	 * @param fullMethodName Full class name (package separated by dots) followed by method name (separated by hash sign).
	 */
	public static void instrument(String fullMethodName) {
		String parts[] = fullMethodName.split("#", 2);
		if (parts.length != 2) {
			return;
		}
		
		instrumentMethod(parts[0], parts[1]);
	}
	
	/** Make a class-loader known to the instrumentation daemon.
	 * 
	 * Only classes loaded via such known class-loaders can be later
	 * instrumented (later = after they are already loaded).
	 * 
	 * @param loader Class loader to register.
	 */
	public static void registerClassLoader(ClassLoader loader) {
		Instrumentator.registerClassLoader(loader);
	}
	
	/** Instrument given method.
	 * 
	 * The instrumentation is scheduled to happen any time after call
	 * to this method.
	 * It is not specified whether the code would be already instrumented
	 * when this method returns.
	 * 
	 * @param className Class name (dot-separated).
	 * @param methodName Method name (without argument specification).
	 */
	public static void instrumentMethod(String className, String methodName) {
		Instrumentator.instrument(className, methodName);
	}
	
	public static void uninstrumentMethod(String className, String methodName) {
		Instrumentator.uninstrument(className, methodName);
	}
	
	private static Map<String, MeasurementPoint> measurements = new HashMap<>();
	
	public static MeasurementPoint getMeasurementPoint(String storageId) {
		MeasurementPoint result = measurements.get(storageId);
		if (result == null) {
			result = new MeasurementPoint(getSampleStorage(storageId),
					Settings.DEFAULT_SKIP_FACTOR);
			measurements.put(storageId, result);
		}
		return result;
	}
	
	private static Map<String, SampleStorage> samples = new HashMap<>();
	
	/**
	 * Clear all stored samples. Use only for testing!
	 */
	public static synchronized void clearAllSamples() {
		samples.clear();
	}
	
	public static synchronized SampleStorage getSampleStorage(String id) {
		SampleStorage result = samples.get(id);
		if (result == null) {
			result = new InMemorySamples(id);
			samples.put(id, result);
		}
		return result;
	}
	
}

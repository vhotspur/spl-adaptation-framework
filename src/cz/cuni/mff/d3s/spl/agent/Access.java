package cz.cuni.mff.d3s.spl.agent;

import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;
import cz.cuni.mff.d3s.spl.agent.InstrumentationDaemon;
import cz.cuni.mff.d3s.spl.core.InMemoryMeasurement;
import cz.cuni.mff.d3s.spl.core.Measurement;

/** Wrapper for accessing instrumentation agent and measurement results. */
public class Access {
	private static Measurement measurement = null;
	
	/** Get and possibly initialize currently used measurement storage.
	 * 
	 * @return Currently used measurement storage.
	 */
	public static Measurement getMeasurement() {
		initMeasurement();
		return measurement;
	}
	
	/** Initialize currently used measurement storage. */
	private static synchronized void initMeasurement() {
		if (measurement != null) {
			return;
		}
		
		measurement = new InMemoryMeasurement();
	}
	
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
		InstrumentationDaemon agent = InstrumentationDaemon.getInstance();
		agent.registerClassLoader(loader);
	}
	
	/** Never instrument given class.
	 * 
	 * The class name can use both dot or slash separated format when
	 * specifying packages (i.e. both
	 * <code>"java/lang/String"</code> and
	 * <code>"java.lang.String"</code> is correct).
	 * 
	 * @param className Fully qualified class name.
	 */
	public static void preventClassInstrumentation(String className)  {
		InstrumentationDaemon agent = InstrumentationDaemon.getInstance();
		agent.preventInstrumentation(className);
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
		InstrumentationDaemon agent = InstrumentationDaemon.getInstance();
		agent.instrument(className, methodName);
	}
	
	/** Guard method for DiSL.
	 * 
	 * This method is not intended for public usage.
	 * 
	 * @param ctx Method static context.
	 * @return Whether the given method shall be instrumented.
	 */
	@GuardMethod
	public static boolean shallInstrumentMethod(MethodStaticContext ctx) {		
		InstrumentationDaemon agent = InstrumentationDaemon.getInstance();
		return agent.shallInstrument(ctx.thisClassName().replace('/', '.'),  ctx.thisMethodName());
	}
}

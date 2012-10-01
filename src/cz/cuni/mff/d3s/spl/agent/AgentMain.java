package cz.cuni.mff.d3s.spl.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;

/** Java agent for run-time instrumentation for SPL. */
public class AgentMain {
	
	/** Agent main method. */
	public static void premain(String args,
			Instrumentation instrumentation) throws Exception {
		Instrumentator.initialize(instrumentation);
		
		final AgentArgumentParser arguments = AgentArgumentParser.create(args);
		
		if (arguments.hasOption("skip.factor")) {
			int value = arguments.getValue("skip.factor", Settings.DEFAULT_SKIP_FACTOR);
			if (value >= 0) {
				Settings.DEFAULT_SKIP_FACTOR = value;
			}
		}
		if (arguments.hasOption("debug")) {
			String[] what = arguments.getValue("debug", "all").split(":");
			for (String debug : what) {
				boolean all = debug.equalsIgnoreCase("all");
				boolean allTransformers = all || debug.equals("transformer");
				if (all || debug.equals("classloader")) {
					Settings.DEBUG_CLASSLOADERS = true;
				}
				if (allTransformers || debug.equals("loading.transformer")) {
					Settings.DEBUG_LOADING_TRANSFORMER = true;
				}
				if (allTransformers || debug.equals("retransforming.transformer")) {
					Settings.DEBUG_RETRANSFORMING_TRANSFORMER = true;
				}
				if (debug.equals("all.class.loading")) {
					Settings.DEBUG_ALL_CLASS_LOADING = true;
				}
			}
		}
		
		String splClass = arguments.getValue("spl.class", null);
		if (splClass != null) {
			Runnable splClassInstance = loadSplClass(splClass);
			if (splClassInstance != null) {
				Thread splThread = new Thread(splClassInstance);
				splThread.setDaemon(true);
				splThread.setName("SPL-user-start");
				splThread.start();
			}
		}
		String splShutdownClass = arguments.getValue("spl.shutdown.class", null);
		if (splShutdownClass != null) {
			Runnable instance = loadSplClass(splShutdownClass);
			if (instance != null) {
				Thread thread = new Thread(instance);
				thread.setName("SPL-user-shutdown");
				Runtime.getRuntime().addShutdownHook(thread);
			}
		}
	}
	
	/** Instantiate checking class.
	 * 
	 * This method silently hides all errors.
	 * 
	 * @param classname Class name, possibly with argument (separated by colon).
	 * @return Loaded class or null.
	 */
	private static Runnable loadSplClass(String classname) {
		Class<?> klass;
		String[] klassAndArgs = classname.split(":", 2);
		try {
			klass = Class.forName(klassAndArgs[0]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		Object instance = null;
		/*
		 * First try whether the class has constructor that accepts
		 * String argument.
		 * If that causes an exception, we will try to use the
		 * default constructor.
		 * Should that fail, we return null.
		 */
		try {
			Constructor<?> ctor = klass.getConstructor(String.class);
			String arg = klassAndArgs.length == 1 ? null : klassAndArgs[1];
			instance = ctor.newInstance(arg);
		} catch (Exception ignored) {
			/* Time to try the default constructor. */
			try {
				instance = klass.newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		
		if (!(instance instanceof Runnable)) {
			return null;
		}
		
		return (Runnable) instance;
	}
}
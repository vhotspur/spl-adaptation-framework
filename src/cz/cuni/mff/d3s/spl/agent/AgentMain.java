package cz.cuni.mff.d3s.spl.agent;

import java.lang.instrument.Instrumentation;

/** Java agent for run-time instrumentation for SPL. */
public class AgentMain {
	
	/** Agent main method. */
	public static void premain(String args,
			Instrumentation instrumentation) throws Exception {
		Instrumentator.initialize(instrumentation);
		
		final AgentArgumentParser arguments = AgentArgumentParser.create(args);
		
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
	
	/** Instantiate checking class. */
	private static Runnable loadSplClass(String classname) {
		Class<?> klass;
		try {
			klass = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		Object instance;
		try {
			instance = klass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		if (!(instance instanceof Runnable)) {
			return null;
		}
		
		return (Runnable) instance;
	}
}
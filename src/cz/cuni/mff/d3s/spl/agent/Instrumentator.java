package cz.cuni.mff.d3s.spl.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;

class Instrumentator {
	private static Instrumentation instrumentationEngine;
	private static Set<ClassLoader> knownClassLoaders;
	private static SplTransformer loadingTransformer;
	private static SplTransformer instrumentingTransformer;
	private static Set<String> instrumentedMethods;
	private static boolean initialized = false;
	
	public static synchronized void initialize(Instrumentation instrumentation) {
		if (initialized) {
			return;
		}
		
		instrumentationEngine = instrumentation;
		
		Map<ClassLoader, Boolean> setBackend = new WeakHashMap<>();
		knownClassLoaders = Collections.newSetFromMap(setBackend);
		
		InstrumentedMethods accessor = new WhatToInstrument();
		
		loadingTransformer = new JavassistInitialTransformer(accessor, new TransformerAddMeasurementPointFields());
		instrumentingTransformer = new JavassistRetransformingTransformer(accessor, new TransformerAddMeasuringCode());
		
		instrumentationEngine.addTransformer(loadingTransformer, false);
		instrumentationEngine.addTransformer(instrumentingTransformer, true);
		
		loadingTransformer.enable();
		instrumentingTransformer.enable();
		
		instrumentedMethods = new ConcurrentSkipListSet<>();
		
		initialized = true;
	}
	
	public static void registerClassLoader(ClassLoader loader) {
		synchronized (knownClassLoaders) {
			registerClassLoaderInternal(loader, true);
		}
	}
	
	public static void instrument(String klass, String method) {
		instrumentedMethods.add(makeFullMethodName(klass, method));
		reinstrument(klass);
	}
	
	public static void uninstrument(String klass, String method) {
		instrumentedMethods.remove(makeFullMethodName(klass, method));
	}
	
	private static class WhatToInstrument implements InstrumentedMethods {
		@Override
		public boolean instrumentMethod(String classname, String methodname) {
			return instrumentedMethods.contains(makeFullMethodName(classname, methodname));
		}
	}
	
	private static String makeFullMethodName(String classname, String methodname) {
		return String.format("%s#%s", classname.replace('/', '.'), methodname);
	}
	
	private static void registerClassLoaderInternal(ClassLoader loader, boolean registerParent) {
		if (loader == null) {
			return;
		}

		boolean newLoader;
		synchronized (knownClassLoaders) {
			newLoader = knownClassLoaders.add(loader);
		}

		if (newLoader) {
			if (registerParent) {
				registerClassLoaderInternal(loader.getParent(), false);
			}
			System.out.printf("Registered class loader %s (parent = %s).\n",
					loader, loader.getParent());
		}
	}
	
	private static void reinstrument(String klass) {
		/*
		 * Make a copy to prevent concurrent modification exception.
		 * 
		 * The cause is that class loading may trigger new class loader
		 * registration. As here we would be inside for-each loop, the
		 * collection would be modified. Creating a copy is the easiest
		 * way for dealing with this.
		 */
		Set<ClassLoader> classLoadersCopy = new HashSet<>();
		synchronized (knownClassLoaders) {
			classLoadersCopy.addAll(knownClassLoaders);
		}
		
		for (ClassLoader cl : classLoadersCopy) {
			reloadClass(klass, cl);
		}
	}

	private static boolean reloadClass(String className, ClassLoader loader) {
		Class<?> klass;
		try {
			klass = loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			/*
			 * Not a problem, we expect that this could happen.
			 */
			return false;
		}

		assert (klass != null);

		
		try {
			instrumentationEngine.retransformClasses(klass);
		} catch (Exception e) {
			reportException(e, "retransformation of %s failed.", klass.getName());
		}
		
		return true;
	}

	private static void reportException(Throwable e, String msg, Object... args) {
		System.err.printf("InstrumentationDaemon: " + msg + "\n", args);
		e.printStackTrace(System.err);
	}
}

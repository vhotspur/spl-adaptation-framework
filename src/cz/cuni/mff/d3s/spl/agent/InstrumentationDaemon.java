package cz.cuni.mff.d3s.spl.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 
 */
class InstrumentationDaemon implements Runnable {
	private static InstrumentationDaemon instance = null;

	/** Create instrumentation daemon and start it in background. */
	public static synchronized void create(Instrumentation instrumentation)
			throws Exception {
		if (instance != null) {
			return;
		}

		instance = new InstrumentationDaemon(instrumentation);

		Thread thread = new Thread(instance);
		thread.setDaemon(true);
		thread.setName("SPL-instrumentation-daemon");
		thread.start();
	}

	/** Get reference to running daemon. */
	public static InstrumentationDaemon getInstance() {
		return instance;
	}

	/** Class-loaders to use when reloading a class. */
	private Set<ClassLoader> knownClassLoaders;

	/** List of classes that shall be reloaded. */
	private BlockingQueue<Class<?>> classesToReload;

	/** Class transformer to use. */
	private Transformer transformer;

	/** Instrumentation service to use. */
	private Instrumentation instrumentation;

	/** List of methods that shall be instrumented. */
	private Set<String> methodsToInstrument;

	private InstrumentationDaemon(Instrumentation instr) throws Exception {
		knownClassLoaders = Collections
				.newSetFromMap(new WeakHashMap<ClassLoader, Boolean>());
		classesToReload = new ArrayBlockingQueue<>(100);
		methodsToInstrument = new HashSet<>();

		instrumentation = instr;
		transformer = new Transformer(NoReloadSnippets.class);
		transformer.enableTransformation();
		instrumentation.addTransformer(transformer, true);
	}

	/** Register a class loader. */
	public void registerClassLoader(ClassLoader loader) {
		if (loader == null) {
			return;
		}

		boolean newLoader;
		synchronized (knownClassLoaders) {
			newLoader = knownClassLoaders.add(loader);
		}

		if (newLoader) {
			System.out.printf("Registered class loader %s (parent = %s).\n",
					loader, loader.getParent());
		}
	}

	/**
	 * Ask for instrumentation of given method in a class.
	 * 
	 * @param className Full class name (dot-separated).
	 * @param methodName Method name (without argument specification).
	 */
	public void instrument(String className, String methodName) {
		String fullName = String.format("%s#%s", className, methodName);
		
		synchronized (methodsToInstrument) {
			methodsToInstrument.add(fullName);			
		}

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
			reloadClass(className, cl);
		}
	}
	
	public void preventInstrumentation(String className) {
		transformer.preventTransformationOnClass(className);
	}
	
	public synchronized boolean shallInstrument(String className, String methodName) {
		return methodsToInstrument.contains(String.format("%s#%s", className, methodName));
	}

	private boolean reloadClass(String className, ClassLoader loader) {
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
			classesToReload.put(klass);
		} catch (InterruptedException e) {
			/*
			 * This shall not happen. We ought to report this.
			 */
			reportException(
					e,
					"blocking queue operation interrupted [%s (from loader %s) won't be instrumented].",
					className, loader);
			return false;
		}

		return true;
	}

	/** Wait for requests for instrumentation a execute them. */
	@Override
	public void run() {
		while (true) {
			Class<?> classToTransform;

			/*
			 * There is no reason why taking from the queue should fail. Thus,
			 * in case of problems we simply try again.
			 */
			try {
				classToTransform = classesToReload.take();
			} catch (InterruptedException e) {
				reportException(e, "taking from blocking queue failed.");
				continue;
			}

			/*
			 * Enable the actual transformation.
			 */
			transformer.enableTransformation();

			/*
			 * Run the actual transformation.
			 * 
			 * Need to print errors as the transformations are actually
			 * happening asynchronously.
			 */
			try {
				if (true) {
					continue;
				}
				instrumentation.retransformClasses(classToTransform);
			} catch (UnmodifiableClassException e) {
				reportException(e, "retransformation failed.");
			}
		}
	}

	/** Convenient wrapper for reporting exceptions in a unified way. */
	private void reportException(Exception e, String msg, Object... args) {
		System.err.printf("InstrumentationDaemon: " + msg + "\n", args);
		e.printStackTrace(System.err);
	}
}

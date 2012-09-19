package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

class JavassistTransformer extends SplTransformer {
	private static final boolean VERBOSE = true;
	private static final boolean VERY_VERBOSE = false;
	
	private final String PREFIX = "______SPL_adapt__";
	private Set<String> alreadyInstrumentedMethods = new HashSet<>();
	
	public JavassistTransformer() {
	}
	
	private String formatJavaCode(String code) {
		String result = code.replace(";", ";\n");
		result = result.replace("{", "{\n");
		result = result.replace("}}", "}\n}");
		return result;
	}

	private void transformMethod(CtMethod method) {
		if (VERBOSE) {
			System.err.printf("transformMethod %s\n", method.getLongName());
		}
		try {
			method.addLocalVariable(PREFIX + "skip", CtClass.booleanType);
			method.addLocalVariable(PREFIX + "startTime", CtClass.longType);
			method.addLocalVariable(PREFIX + "endTime", CtClass.longType);
			
			String dataSourceId = InstrumentingDataSource.createId(method.getDeclaringClass().getName(), method.getName());
			
			String codeBefore = "{"
				//+ "System.err.print(\"BEFORE\\n\");"
				+ "cz.cuni.mff.d3s.spl.agent.Access.counter++;"
				+ "if (cz.cuni.mff.d3s.spl.agent.Access.counter < 0) {"
				+ PREFIX + "skip = true;"
				+ PREFIX + "startTime = 0;"
				+ "} else {"
				+ PREFIX + "skip = false;"
				+ "cz.cuni.mff.d3s.spl.agent.Access.counter = 0;"
				+ PREFIX + "startTime = System.nanoTime();"
				+ "}"
				+ "}";
			String codeAfter = "{"
				//+ "System.err.print(\"AFTER\\n\");"
				+ "if (!" + PREFIX + "skip) {"
				+ PREFIX + "endTime = System.nanoTime();"
				+ "cz.cuni.mff.d3s.spl.agent.Access.getSampleStorage(\"" + dataSourceId + "\").addFromNanoTimeRange(" + PREFIX + "startTime, " + PREFIX + "endTime);"
				+ "}"
				+ "}";
			
			if (VERY_VERBOSE) {
				System.err.printf("BEFORE: %s\n", formatJavaCode(codeBefore));
				System.err.printf("AFTER: %s\n", formatJavaCode(codeAfter));
			}
			
			method.insertBefore(codeBefore);
			method.insertAfter(codeAfter, false);
			
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get Javassist class representation from class bytecode.
	 * 
	 * @param classname
	 *            Class name (dot separated).
	 * @param bytecode
	 *            Class bytecode.
	 * @return Javassist class or null on error.
	 */
	private CtClass classFromBytecode(String classname, byte[] bytecode) {
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ByteArrayClassPath(classname, bytecode));
		try {
			return pool.get(classname);
		} catch (NotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Transform the class. */
	@Override
	public synchronized byte[] transform(ClassLoader loader, String classname,
			Class<?> theClass, ProtectionDomain domain, byte[] bytecode)
			throws IllegalClassFormatException {
		boolean continueTransformation = beforeTransform(loader, classname);

		if (!continueTransformation) {
			return null;
		}

		/* Javassist uses dot-separated names. */
		String dotClassname = classname.replace('/', '.');

		/* Load the class and defrost it for transformation. */
		CtClass cc = classFromBytecode(dotClassname, bytecode);
		if (cc == null) {
			return null;
		}
		cc.defrost();
		
		/*
		 * Instrument individual methods.
		 *  
		 * FIXME:
		 * - instrument the class from different class loaders
		 * - for unknown reason, removing the somethingChanged = true
		 *   in the loop body renders the transformation useless for
		 *   Newton example
		 */
		InstrumentationDaemon agent = InstrumentationDaemon.getInstance();
		CtMethod[] methods = cc.getMethods();
		boolean somethingChanged = false;
		for (CtMethod m : methods) {
			/* Only methods declared here. */
			if (!m.getLongName().startsWith(dotClassname)) {
				continue;
			}
			/* Shall we instrument this one? */
			if (!agent.shallInstrument(dotClassname, m.getName())) {
				continue;
			}
			
			// FIXME: need to investigate why this matters
			somethingChanged = true;
			
			/* Do not instrument twice. */
			if (alreadyInstrumentedMethods.contains(m.getLongName())) {
				continue;
			}
			
			transformMethod(m);
			somethingChanged = true;
			
			alreadyInstrumentedMethods.add(m.getLongName());
		}

		if (!somethingChanged) {
			return null;
		}
		
		byte[] transformedBytecode;
		try {
			transformedBytecode = cc.toBytecode();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (CannotCompileException e) {
			e.printStackTrace();
			return null;
		}
		
		/* Dump the class file to disk. */
		if (VERY_VERBOSE) {
			try {
				cc.writeFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (VERBOSE) {
			System.err.printf("Returning transformed bytecode of %s @ %s (%dB vs. orig %dB)\n", dotClassname, loader, transformedBytecode.length, bytecode.length);
		}
		
		return transformedBytecode;
	}
}

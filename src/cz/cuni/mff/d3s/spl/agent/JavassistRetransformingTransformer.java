package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;

class JavassistRetransformingTransformer extends JavassistTransformer {
	private JavassistInstrumentingTransformer transformer;
	private InstrumentedMethods instrumentedMethods = null;
	
	public JavassistRetransformingTransformer(InstrumentedMethods instrumentedMethods, JavassistInstrumentingTransformer transformer) {
		this.instrumentedMethods = instrumentedMethods;
		this.transformer = transformer;
	}
	
	/** Transform the class. */
	@Override
	public synchronized byte[] transform(ClassLoader loader, String classname,
			Class<?> theClass, ProtectionDomain domain, byte[] bytecode)
			throws IllegalClassFormatException {
		boolean continueTransformation = beforeTransform(loader, classname, theClass);

		if (!continueTransformation) {
			return null;
		}
		
		/* Do nothing if there is no method to be instrumented. */
		if (!instrumentedMethods.instrumentClass(classname)) {
			return null;
		}

		/* Javassist uses dot-separated names. */
		String dotClassname = classname.replace('/', '.');

		/* Load the class and defrost it for transformation. */
		CtClass cc = classFromBytecode(dotClassname, bytecode);
		if (cc == null) {
			return null;
		}
		
		/* Instrument individual methods. */
		CtMethod[] methods = cc.getMethods();
		for (CtMethod m : methods) {
			/* Only methods declared here. */
			if (!m.getLongName().startsWith(dotClassname)) {
				continue;
			}
			
			/* Shall we instrument this one? */
			if (!instrumentedMethods.instrumentMethod(dotClassname, m.getName())) {
				continue;
			}
			
			if (Settings.DEBUG_RETRANSFORMING_TRANSFORMER) {
				Settings.log.printf("Retransforming method %s.\n", m.getLongName());
			}
			transformer.transform(m);
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
		
		/*
		 * Without detaching, subsequent calls to the default class pool
		 * would return the already modified class.
		 * This ensures that the class is created in a "fresh" copy.
		 */
		cc.detach();
		
		return transformedBytecode;
	}
}

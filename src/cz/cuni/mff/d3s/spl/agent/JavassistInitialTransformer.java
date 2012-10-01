package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

class JavassistInitialTransformer extends JavassistTransformer {
	private JavassistFirstClassLoadTransformer transformer = null;
	private InstrumentedMethods instrumentedMethods = null;
	
	public JavassistInitialTransformer(InstrumentedMethods instrumentedMethods, JavassistFirstClassLoadTransformer transformer) {
		this.instrumentedMethods = instrumentedMethods;
		this.transformer = transformer;
	}
	
	/** Transform the class. */
	@Override
	public synchronized byte[] transform(ClassLoader loader, String classname,
			Class<?> theClass, ProtectionDomain domain, byte[] bytecode)
			throws IllegalClassFormatException {
		try {
			return throwingTransform(loader, classname, theClass, domain, bytecode);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	
	private synchronized byte[] throwingTransform(ClassLoader loader, String classname,
			Class<?> theClass, ProtectionDomain domain, byte[] bytecode) throws NotFoundException, IOException, CannotCompileException {	
		boolean continueTransformation = beforeTransform(loader, classname, theClass);

		if (!continueTransformation) {
			if (Settings.DEBUG_WATCH_CLASS(classname)) {
				Settings.log.printf("Shall not continue in transformation of %s.\n", classname);
			}
			return null;
		}
		
		if (theClass != null) {
			return null;
		}
		
		/* Do nothing if there is no method to be instrumented. */
		if (!instrumentedMethods.instrumentClass(classname)) {
			if (Settings.DEBUG_WATCH_CLASS(classname)) {
				Settings.log.printf("No need to transform %s.\n", classname);
			}
			return null;
		}

		/* Javassist uses dot-separated names. */
		String dotClassname = classname.replace('/', '.');

		/* Load the class and defrost it for transformation. */
		CtClass cc = classFromBytecode(dotClassname, bytecode);
		
		/* Instrument the class as a whole. */
		if (Settings.DEBUG_LOADING_TRANSFORMER) {
			Settings.log.printf("Transforming class %s (first load).\n", dotClassname);
		}
		transformer.transform(cc);
		
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
			
			if (Settings.DEBUG_LOADING_TRANSFORMER) {
				Settings.log.printf("Transforming method %s (first load).\n", m.getLongName());
			}	
			transformer.transform(m);
		}
				
		byte[] transformedBytecode = cc.toBytecode();
		
		return transformedBytecode;
	}
}

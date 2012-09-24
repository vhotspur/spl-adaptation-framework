package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;

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
		boolean continueTransformation = beforeTransform(loader, classname);

		if (!continueTransformation) {
			return null;
		}
		
		if (theClass != null) {
			return null;
		}

		/* Javassist uses dot-separated names. */
		String dotClassname = classname.replace('/', '.');

		/* Load the class and defrost it for transformation. */
		CtClass cc = classFromBytecode(dotClassname, bytecode);
		if (cc == null) {
			return null;
		}
		
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
		
		return transformedBytecode;
	}
}

package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;

class JavassistRetransformingTransformer extends JavassistTransformer {
	private JavassistInstrumentingTransformer transformer;
	
	public JavassistRetransformingTransformer(JavassistInstrumentingTransformer transformer) {
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

		/* Javassist uses dot-separated names. */
		String dotClassname = classname.replace('/', '.');

		/* Load the class and defrost it for transformation. */
		CtClass cc = classFromBytecode(dotClassname, bytecode);
		if (cc == null) {
			return null;
		}
		
		/* Instrument individual methods. */
		InstrumentationDaemon agent = InstrumentationDaemon.getInstance();
		CtMethod[] methods = cc.getMethods();
		for (CtMethod m : methods) {
			/* Only methods declared here. */
			if (!m.getLongName().startsWith(dotClassname)) {
				continue;
			}
			/* Shall we instrument this one? */
			if (!agent.shallInstrument(dotClassname, m.getName())) {
				continue;
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

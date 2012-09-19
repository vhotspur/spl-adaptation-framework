package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

class JavassistInitialTransformer extends JavassistTransformer {
	private List<JavassistFirstClassLoadTransformer> transformers;
	
	public JavassistInitialTransformer() {
		transformers = new LinkedList<>();
	}
	
	public void addTransformer(JavassistFirstClassLoadTransformer transformer) {
		transformers.add(transformer);
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
		
		for (JavassistFirstClassLoadTransformer t : transformers) {
			t.transform(cc);
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

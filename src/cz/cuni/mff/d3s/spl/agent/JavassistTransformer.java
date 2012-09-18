package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.d3s.spl.core.data.SampleStorage;
import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

class JavassistTransformer extends SplTransformer {
	private final String PREFIX = "______SPL_adapt__";
	private Set<String> alreadyInstrumentedMethods = new HashSet<>();
	
	public JavassistTransformer() {
	}

	private void transformMethod(CtMethod method) {
		String id = method.getLongName();
		if (alreadyInstrumentedMethods.contains(id)) {
			return;
		}
		
		System.err.printf("transformMethod(%s)\n", method.getLongName());
		
		try {
			method.addLocalVariable(PREFIX + "skip", CtClass.booleanType);
			method.addLocalVariable(PREFIX + "startTime", CtClass.longType);
			method.addLocalVariable(PREFIX + "endTime", CtClass.longType);
			method.addLocalVariable(PREFIX + "diff", CtClass.longType);
			
			String dataSourceId = InstrumentingDataSource.createId(method.getDeclaringClass().getName(), method.getName());
			
			String codeBefore = "{"
				+ "cz.cuni.mff.d3s.spl.agent.Access.counter++;"
				+ "if (cz.cuni.mff.d3s.spl.agent.Access.counter < 0) {"
				+ PREFIX + "skip = true;"
				+ "} else {"
				+ PREFIX + "skip = false;"
				+ "cz.cuni.mff.d3s.spl.agent.Access.counter = 0;"
				+ PREFIX + "startTime = System.nanoTime();"
				+ "}"
				+ "}";
			String codeAfter = "{"
				+ "if (!" + PREFIX + "skip) {"
				+ PREFIX + "endTime = System.nanoTime();"
				+ PREFIX + "diff = " + PREFIX + "endTime - " + PREFIX + "startTime;"
				+ "cz.cuni.mff.d3s.spl.agent.Access.getSampleStorage(\"" + dataSourceId + "\").addFromNanoTimeRange(" + PREFIX + "startTime, " + PREFIX + "endTime);"
				+ "}"
				+ "}";
			
			method.insertBefore(codeBefore);
			method.insertAfter(codeAfter, false);
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
		
		alreadyInstrumentedMethods.add(id);
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

		String dotClassname = classname.replace('/', '.');
		InstrumentationDaemon agent = InstrumentationDaemon.getInstance();

		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ByteArrayClassPath(dotClassname, bytecode));
		CtClass cc;
		try {
			cc = pool.get(dotClassname);
		} catch (NotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		cc.defrost();

		CtMethod[] methods = cc.getMethods();
		boolean somethingChanged = false;
		for (CtMethod m : methods) {
			if (!m.getLongName().startsWith(dotClassname)) {
				continue;
			}
			if (!agent.shallInstrument(dotClassname, m.getName())) {
				continue;
			}

			transformMethod(m);
			somethingChanged = true;
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
		
		System.err.printf("Returning transformed bytecode of %s\n", dotClassname);
		
		return transformedBytecode;
	}
}

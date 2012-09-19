package cz.cuni.mff.d3s.spl.agent;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;

public class TransformerAddMeasuringCode implements
		JavassistInstrumentingTransformer {
	
	private static String PREFIX = JavassistTransformer.NEW_IDENTIFIERS_PREFIX + "local_";

	@Override
	public void transform(CtMethod method) {
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
			
			method.insertBefore(codeBefore);
			method.insertAfter(codeAfter, false);
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

}

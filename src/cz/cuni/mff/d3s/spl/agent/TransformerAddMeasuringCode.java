package cz.cuni.mff.d3s.spl.agent;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class TransformerAddMeasuringCode implements
		JavassistInstrumentingTransformer {
	
	private static String PREFIX = JavassistTransformer.NEW_IDENTIFIERS_PREFIX + "local_";

	@Override
	public void transform(CtMethod method) {
		try {
			try {
				CtClass measurementPointClass = method.getDeclaringClass().getClassPool().get("cz.cuni.mff.d3s.spl.core.data.MeasurementPoint");
				method.addLocalVariable(PREFIX + "point", measurementPointClass);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			method.addLocalVariable(PREFIX + "skip", CtClass.booleanType);
			method.addLocalVariable(PREFIX + "startTime", CtClass.longType);
			method.addLocalVariable(PREFIX + "endTime", CtClass.longType);
			
			
			String dataSourceId = InstrumentingDataSource.createId(method.getDeclaringClass().getName(), method.getName());
			
			String codeBefore = "{"
				//+ "System.err.print(\"BEFORE\\n\");"
				+ PREFIX + "point = cz.cuni.mff.d3s.spl.agent.Access.getMeasurementPoint(\"" + dataSourceId + "\");"
				+ PREFIX + "skip = ! " + PREFIX + "point.next();"
				+ "if (" + PREFIX + "skip) {"
				+ PREFIX + "startTime = 0;"
				+ "} else {"
				+ PREFIX + "startTime = System.nanoTime();"
				+ "}"
				+ "}";
			String codeAfter = "{"
				//+ "System.err.print(\"AFTER\\n\");"
				+ "if (!" + PREFIX + "skip) {"
				+ PREFIX + "endTime = System.nanoTime();"
				+ PREFIX + "point.getStorage().addFromNanoTimeRange(" + PREFIX + "startTime, " + PREFIX + "endTime);"
				+ "}"
				+ "}";
			
			method.insertBefore(codeBefore);
			method.insertAfter(codeAfter, false);
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

}

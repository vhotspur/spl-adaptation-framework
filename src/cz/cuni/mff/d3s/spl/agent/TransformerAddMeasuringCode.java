package cz.cuni.mff.d3s.spl.agent;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

public class TransformerAddMeasuringCode implements
		JavassistInstrumentingTransformer {
	
	private static String PREFIX = JavassistTransformer.NEW_IDENTIFIERS_PREFIX + "local_";

	@Override
	public void transform(CtMethod method) {
		try {
			CtClass klass = method.getDeclaringClass();
			String pointVariable;
			String pointVariableInitialization;
			/*
			 * CtClass has no method hasField(String fieldName) so we
			 * replace the if-else block with try-catch.
			 */
			try {
				if (Modifier.isStatic(method.getModifiers())) {
					throw new NotFoundException("Cannot use non-static fields in a static method.");
				}
				
				String pointFieldName = JavassistTransformer.NEW_IDENTIFIERS_PREFIX + "point_" + method.getName();
				
				@SuppressWarnings("unused")
				CtField ignore = klass.getField(pointFieldName);
				pointVariable = pointFieldName;
				pointVariableInitialization = "";
			} catch (NotFoundException e) {
				/* No such field, need to create local variable and initialize it. */
				pointVariable = PREFIX + "point";
				try {
					CtClass measurementPointClass = method.getDeclaringClass().getClassPool().get("cz.cuni.mff.d3s.spl.core.data.MeasurementPoint");
					method.addLocalVariable(pointVariable, measurementPointClass);
				} catch (NotFoundException e2) {
					e2.printStackTrace();
				}
				String dataSourceId = InstrumentingDataSource.createId(method.getDeclaringClass().getName(), method.getName());
				pointVariableInitialization = PREFIX + "point = cz.cuni.mff.d3s.spl.agent.Access.getMeasurementPoint(\"" + dataSourceId + "\");";
			}
			method.addLocalVariable(PREFIX + "skip", CtClass.booleanType);
			method.addLocalVariable(PREFIX + "startTime", CtClass.longType);
			method.addLocalVariable(PREFIX + "endTime", CtClass.longType);
			
			String codeBefore = "{"
				//+ "System.err.print(\"BEFORE\\n\");"
				+ pointVariableInitialization
				+ PREFIX + "skip = ! " + pointVariable + ".next();"
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
				+ pointVariable + ".addFromNanoTimeRange(" + PREFIX + "startTime, " + PREFIX + "endTime);"
				+ "}"
				+ "}";
			
			method.insertBefore(codeBefore);
			method.insertAfter(codeAfter, false);
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

}

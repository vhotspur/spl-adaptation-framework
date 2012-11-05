/*
 * Copyright 2012 Charles University in Prague
 * Copyright 2012 Vojtech Horky
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

package cz.cuni.mff.d3s.spl.agent;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;

public class TransformerAddMeasurementPointFields implements
		JavassistFirstClassLoadTransformer {

	private static final String MEASUREMENT_POINT_VARIABLE = 
		JavassistTransformer.NEW_IDENTIFIERS_PREFIX + "point_";
	
	@Override
	public void transform(CtClass klass) {
		/* Do nothing. */
	}
	
	@Override
	public boolean shallTransformMethods(CtClass klass) {
		if (klass.isInterface()) {
			return false;
		}
		
		CtClass[] interfaces;
		try {
			interfaces = klass.getInterfaces();
		} catch (NotFoundException e) {
			// FIXME: what to do with the error?
			e.printStackTrace();
			return false;
		}
		for (CtClass iface : interfaces) {
			if (iface.getName().equals("java.io.Serializable")) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void transform(CtMethod method) {
		CtClass klass = method.getDeclaringClass();
		String dataSourceId = InstrumentingDataSource.createId(klass.getName(), method.getName());
		String fieldName = MEASUREMENT_POINT_VARIABLE + method.getName();
				
		try {
			CtClass measurementPointClass = method.getDeclaringClass().getClassPool().get("cz.cuni.mff.d3s.spl.core.data.MeasurementPoint");
			CtField field = new CtField(measurementPointClass, fieldName, klass);
			
			klass.addField(field, "cz.cuni.mff.d3s.spl.agent.Access.getMeasurementPoint(\"" + dataSourceId + "\")");
		} catch (DuplicateMemberException e) {
			// FIXME: properly handle overloaded methods
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}
}

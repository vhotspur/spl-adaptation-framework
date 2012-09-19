package cz.cuni.mff.d3s.spl.agent;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;

public class TransformerAddInstrumentationOnOffToClass implements
		JavassistFirstClassLoadTransformer {

	private static String ON_OFF_FIELD_NAME
		= JavassistTransformer.NEW_IDENTIFIERS_PREFIX + "instrument";

	@Override
	public void transform(CtClass klass) {
		if (klass.isInterface()) {
			return;
		}
		try {
			CtField field = new CtField(CtClass.booleanType, ON_OFF_FIELD_NAME, klass);
			klass.addField(field, "false");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

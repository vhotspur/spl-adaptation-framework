package cz.cuni.mff.d3s.spl.agent;

import javassist.CtClass;

public interface JavassistFirstClassLoadTransformer extends JavassistInstrumentingTransformer {
	void transform(CtClass klass);
	boolean shallTransformMethods(CtClass klass);
}

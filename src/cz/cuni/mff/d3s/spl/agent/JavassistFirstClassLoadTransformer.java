package cz.cuni.mff.d3s.spl.agent;

import javassist.CtClass;

public interface JavassistFirstClassLoadTransformer {
	void transform(CtClass klass);
}

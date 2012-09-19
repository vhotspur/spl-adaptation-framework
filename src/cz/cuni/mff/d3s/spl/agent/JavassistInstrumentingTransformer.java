package cz.cuni.mff.d3s.spl.agent;

import javassist.CtMethod;

public interface JavassistInstrumentingTransformer {
	void transform(CtMethod method);
}

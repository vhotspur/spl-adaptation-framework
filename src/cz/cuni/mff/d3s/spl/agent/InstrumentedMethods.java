package cz.cuni.mff.d3s.spl.agent;

public interface InstrumentedMethods {
	boolean instrumentClass(String classname);
	boolean instrumentMethod(String classname, String methodname);
}

package cz.cuni.mff.d3s.spl.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

public abstract class SplTransformer implements ClassFileTransformer {

	private boolean transformationEnabled = false;
	private Set<String> preventInstrumentation = new HashSet<>();
	
	/** Prevent transformation of given class.
	 * 
	 * The class name can use either dots or slashes to separate package
	 * names.
	 * 
	 * @param className Full class name, without wildcards.
	 */
	public void preventTransformationOnClass(String className) {
		preventInstrumentation.add(className.replace('.', '/'));
	}
	
	public void enable() {
		transformationEnabled = true;
	}
	
	protected final synchronized boolean beforeTransform(ClassLoader loader,
			String classname) {
		if (!transformationEnabled) {
			return false;
		}
		
		Access.registerClassLoader(loader);
		
		if (preventInstrumentation.contains(classname)) {
			return false;
		}
		
		return true;
	}
}

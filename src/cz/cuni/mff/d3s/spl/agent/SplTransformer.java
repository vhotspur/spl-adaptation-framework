package cz.cuni.mff.d3s.spl.agent;

import java.lang.instrument.ClassFileTransformer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class SplTransformer implements ClassFileTransformer {

	private boolean transformationEnabled = false;
	private Set<String> preventInstrumentation = new HashSet<>();
	private List<String> forbiddenPackages = new LinkedList<>();
	
	public SplTransformer() {
		forbiddenPackages.add("java/");
		forbiddenPackages.add("sun/");
		forbiddenPackages.add("cz/cuni/mff/d3s/spl/agent/");
	}
	
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
		
		Instrumentator.registerClassLoader(loader);
		
		if (preventInstrumentation.contains(classname)) {
			return false;
		}
		
		for (String s : forbiddenPackages) {
			if (classname.startsWith(s)) {
				return false;
			}
		}
		
		return true;
	}
}

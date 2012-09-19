package cz.cuni.mff.d3s.spl.agent;

import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.security.ProtectionDomain;

import ch.usi.dag.disl.DiSL;
import ch.usi.dag.disl.exception.DiSLException;

/** DiSL based instrumentation transformer. */ 
class Transformer extends SplTransformer {
	private DiSL disl;
	
	/** Create new transformer with a snippet class.
	 * 
	 * @param snippetClass Class with instrumentation snippets.
	 * @throws DiSLException DiSL initialization problem.
	 */
	public Transformer(Class<?> snippetClass) throws DiSLException {
		String snippetClassResource = getClassResourceUrl(snippetClass);
		
		/*
		 * Initialize DiSL.
		 * 
		 * Currently, DiSL uses only properties (or manifest) so
		 * we might actually override existing settings.
		 * 
		 * We need to fix this later.
		 */
		System.setProperty("disl.classes", snippetClassResource);

		disl = new DiSL(false);
	}

	/** Create resource path from given class.
	 * 
	 * @param cls Class to use.
	 * @return Fully qualified resource path to the class file.
	 */
	private String getClassResourceUrl(Class<?> cls) {
		String name = cls.getName();
		String filename = name.replace('.', '/') + ".class";
		URL resource = cls.getClassLoader().getResource(filename);
		return resource.toString();
	}

	/** Transform the class with DiSL. */
	@Override
	public byte[] transform(ClassLoader loader, String classname,
			Class<?> theClass, ProtectionDomain domain, byte[] bytecode)
			throws IllegalClassFormatException {
		System.err.printf("transform(\"%s\", %s)\n", classname, theClass);
		boolean okayToContinue = beforeTransform(loader, classname);
		if (!okayToContinue) {
			return null;
		}
		
		try {
			return disl.instrument(bytecode);
		} catch (Throwable e) {
			System.err.printf("DiSL instrumentation failed:\n");
			e.printStackTrace();
			return null;
		}
	}
}

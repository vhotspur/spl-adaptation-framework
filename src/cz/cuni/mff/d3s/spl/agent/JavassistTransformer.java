package cz.cuni.mff.d3s.spl.agent;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

abstract class JavassistTransformer extends SplTransformer {
	public static final String NEW_IDENTIFIERS_PREFIX = "______SPL_adapt__";
	
	public JavassistTransformer() {
	}
	
	/**
	 * Get Javassist class representation from class bytecode.
	 * 
	 * @param classname
	 *            Class name (dot separated).
	 * @param bytecode
	 *            Class bytecode.
	 * @return Javassist class or null on error.
	 * @throws NotFoundException Javassist engine was not able to load the class.
	 */
	protected CtClass classFromBytecode(String classname, byte[] bytecode) throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ByteArrayClassPath(classname, bytecode));
		CtClass cc = pool.get(classname);
		cc.defrost();
		return cc;
	}
}

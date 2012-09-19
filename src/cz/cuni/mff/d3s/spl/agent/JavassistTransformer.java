package cz.cuni.mff.d3s.spl.agent;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.spl.core.data.instrumentation.InstrumentingDataSource;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

abstract class JavassistTransformer extends SplTransformer {
	private static final boolean VERBOSE = true;
	private static final boolean VERY_VERBOSE = false;
	
	public static final String NEW_IDENTIFIERS_PREFIX = "______SPL_adapt__";
	
	private Set<String> alreadyInstrumentedMethods;
	
	public JavassistTransformer() {
		alreadyInstrumentedMethods = new HashSet<>();
	}
	
	/**
	 * Get Javassist class representation from class bytecode.
	 * 
	 * @param classname
	 *            Class name (dot separated).
	 * @param bytecode
	 *            Class bytecode.
	 * @return Javassist class or null on error.
	 */
	protected CtClass classFromBytecode(String classname, byte[] bytecode) {
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ByteArrayClassPath(classname, bytecode));
		try {
			CtClass cc = pool.get(classname);
			cc.defrost();
			return cc;
		} catch (NotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}

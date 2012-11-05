/*
 * Copyright 2012 Charles University in Prague
 * Copyright 2012 Vojtech Horky
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

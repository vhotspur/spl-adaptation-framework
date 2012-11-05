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

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

class JavassistRetransformingTransformer extends JavassistTransformer {
	private JavassistInstrumentingTransformer transformer;
	private InstrumentedMethods instrumentedMethods = null;
	
	public JavassistRetransformingTransformer(InstrumentedMethods instrumentedMethods, JavassistInstrumentingTransformer transformer) {
		this.instrumentedMethods = instrumentedMethods;
		this.transformer = transformer;
	}
	
	/** Transform the class. */
	@Override
	public byte[] transform(ClassLoader loader, String classname,
			Class<?> theClass, ProtectionDomain domain, byte[] bytecode)
			throws IllegalClassFormatException {
		try {
			return throwingTransform(loader, classname, theClass, domain, bytecode);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
		
	private synchronized byte[] throwingTransform(ClassLoader loader, String classname,
			Class<?> theClass, ProtectionDomain domain, byte[] bytecode) throws NotFoundException, IOException, CannotCompileException {
		boolean continueTransformation = beforeTransform(loader, classname, theClass);

		if (!continueTransformation) {
			if (Settings.DEBUG_WATCH_CLASS(classname)) {
				Settings.log.printf("Shall not continue in transformation of %s.\n", classname);
			}
			return null;
		}
		
		/* Do nothing if there is no method to be instrumented. */
		if (!instrumentedMethods.instrumentClass(classname)) {
			if (Settings.DEBUG_WATCH_CLASS(classname)) {
				Settings.log.printf("No need to transform %s.\n", classname);
			}
			return null;
		}

		/* Javassist uses dot-separated names. */
		String dotClassname = classname.replace('/', '.');

		/* Load the class and defrost it for transformation. */
		CtClass cc = classFromBytecode(dotClassname, bytecode);
		
		/* Instrument individual methods. */
		CtMethod[] methods = cc.getMethods();
		for (CtMethod m : methods) {
			/* Only methods declared here. */
			if (!m.getLongName().startsWith(dotClassname)) {
				continue;
			}
			
			/* Shall we instrument this one? */
			if (!instrumentedMethods.instrumentMethod(dotClassname, m.getName())) {
				continue;
			}
			
			if (Settings.DEBUG_RETRANSFORMING_TRANSFORMER) {
				Settings.log.printf("Retransforming method %s.\n", m.getLongName());
			}
			transformer.transform(m);
		}
		
		byte[] transformedBytecode = cc.toBytecode();
		
		/*
		 * Without detaching, subsequent calls to the default class pool
		 * would return the already modified class.
		 * This ensures that the class is created in a "fresh" copy.
		 */
		cc.detach();
		
		return transformedBytecode;
	}
}

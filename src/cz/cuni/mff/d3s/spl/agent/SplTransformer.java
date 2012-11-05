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
			String classname, Class<?> klass) {
		if (Settings.DEBUG_ALL_CLASS_LOADING || Settings.DEBUG_WATCH_CLASS(classname)) {
			Settings.log.printf("%s: loading %s (%s @ %s).\n", this.getClass().getSimpleName(),
					classname, klass, loader);
		}

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

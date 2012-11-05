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
package cz.cuni.mff.d3s.spl.core.data;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


public class DataSourceRegister {

	private static Map<String, DataSource> register = new HashMap<>();
	
	public static void put(String id, DataSource source) {
		if ((id == null) || (source == null)) {
			throw new IllegalArgumentException("Parameter cannot be null.");
		}
		synchronized (register) {
			register.put(id, source);			
		}
	}
	
	/** Retrieve previously stored data source.
	 * 
	 * This function always returns a valid data source (or throws an
	 * exception).
	 * 
	 * @param id Data source id.
	 * @return Data source bound with given id.
	 * @throw NoSuchElementException No data source with given id was stored.
	 */
	public static synchronized DataSource get(String id) {
		DataSource source;
		synchronized (register) {
			source = register.get(id);	
		}
		if (source == null) {
			String msg = String.format("No data source `%s' registered.", id);
			throw new NoSuchElementException(msg);
		}
		return source;
	}
}

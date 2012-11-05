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
package cz.cuni.mff.d3s.spl.core.formula;

import java.util.NoSuchElementException;

import cz.cuni.mff.d3s.spl.core.data.DataSource;

/**
 * SPL formula representation.
 * 
 * This interface represents the parsed formula without any binding to a data
 * source. Once the binding is done, user can evaluate the formula.
 * 
 */
public interface Formula {
	/**
	 * Bind variable from a formula with a concrete data source.
	 * 
	 * @param variable
	 *            Variable name as written in the formula.
	 * @param data
	 *            Data source to bind variable with.
	 * @throws NoSuchElementException
	 *             No such variable in the formula.
	 */
	void bind(String variable, DataSource data) throws NoSuchElementException;

	/**
	 * Evaluate the formula.
	 * 
	 * @return Result of the evaluation.
	 */
	Result evaluate();
}

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

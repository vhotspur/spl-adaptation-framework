package cz.cuni.mff.d3s.spl.example.newton.app;

import java.util.Random;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.analysis.solvers.NewtonSolver;

public class Main {
	public static final int MAX_POLYNOM_DEGREE = 10;
	public static final double COEFFICIENT_MULTIPLIER = 25.0;
	public static final int WARM_UP_LOOPS = 10000;
	public static final int MEASURED_LOOPS = 1000000;
	

	public static void main(String[] args) {
		try {
			System.out.printf("Letting things settle down before actual computation.\n");
			Thread.sleep(1000 * 3);
		} catch (InterruptedException ignored) {
		}
		
		/* This seed gives reasonable data, keep it at that ;-). */
		Random random = new Random(2);
		double[] coefficients = generateCoefficients(random);
		
		PolynomialFunction function = new PolynomialFunction(coefficients);
		NewtonSolver solver = new NewtonSolver();
		
		System.out.printf("Will solve polynomila function of degree %d.\n", function.degree());
			
		try {
			for (int i = 0; i < WARM_UP_LOOPS; i++) {
				double result = solver.solve(10000, function, -1000, 1000);
			}
			long startTimeNanos = System.nanoTime();
			for (int i = 0; i < MEASURED_LOOPS; i++) {
				double result = solver.solve(10000, function, -1000, 1000);
			}
			long endTimeNanos = System.nanoTime();
			long runTimeNanos = endTimeNanos - startTimeNanos;
			long runTimeMillis = runTimeNanos / (1000 * 1000);
			
			System.out.printf("%d loops of solving took %dns (%dms).\n",
					MEASURED_LOOPS, runTimeNanos, runTimeMillis);
		} catch (MaxIterationsExceededException e) {
			e.printStackTrace();
		} catch (FunctionEvaluationException e) {
			e.printStackTrace();
		}
	}
	
	private static double[] generateCoefficients(Random random) {
		int n = random.nextInt(MAX_POLYNOM_DEGREE) + 1;
		double[] result = new double[n];
		for (int i = 0; i < n; i++) {
			double coeff = random.nextDouble() - 0.5;
			result[i] = coeff * COEFFICIENT_MULTIPLIER;
		}
		return result;
	}
}

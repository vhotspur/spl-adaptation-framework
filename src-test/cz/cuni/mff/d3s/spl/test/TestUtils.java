package cz.cuni.mff.d3s.spl.test;

import org.junit.Ignore;
import static org.junit.Assert.*;

import cz.cuni.mff.d3s.spl.core.data.Statistics;

@Ignore
public class TestUtils {
	
	public static void assertStatisticsEqual(Statistics expected, Statistics actual, double delta) {
		assertEquals(expected.getArithmeticMean(), actual.getArithmeticMean(), delta);
		assertEquals(expected.getSampleCount(), actual.getSampleCount());
	}
}

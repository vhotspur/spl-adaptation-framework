package cz.cuni.mff.d3s.spl.test;

import org.junit.Ignore;
import static org.junit.Assert.*;

import cz.cuni.mff.d3s.spl.core.data.Statistics;

@Ignore
public class TestUtils {
	
	public static void assertStatisticsEqual(Statistics a, Statistics b, double delta) {
		assertEquals(a.getArithmeticMean(), b.getArithmeticMean(), delta);
		assertEquals(a.getSampleCount(), b.getSampleCount());
	}
}

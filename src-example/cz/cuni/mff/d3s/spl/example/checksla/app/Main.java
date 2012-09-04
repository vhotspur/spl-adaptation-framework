package cz.cuni.mff.d3s.spl.example.checksla.app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
	private static class NullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
		}
	}

	public String shortMethod(Random random) {
		StringBuilder builder = new StringBuilder();
		int size = random.nextInt(10) + 2;
		for (int i = 0; i < size; i++) {
			builder.append("x");
		}
		return builder.toString();
	}
	
	public String longMethod(Random random) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {
		}
		StringBuilder builder = new StringBuilder();
		int size = random.nextInt(10) + 2;
		for (int i = 0; i < size; i++) {
			builder.append("x");
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		Main me = new Main();
		Random random = new Random();
		
		PrintWriter output = new PrintWriter(new NullOutputStream());
		// output = new PrintWriter(System.out, true);
		
		long endTime = System.currentTimeMillis() + 10 * 1000;
		
		while (System.currentTimeMillis() < endTime) {
			String result = me.shortMethod(random);
			output.printf("shortMethod = %s\n", result);
			result = me.longMethod(random);
			output.printf("longMethod = %s\n", result);
		}
		
		output.close();
	}

}

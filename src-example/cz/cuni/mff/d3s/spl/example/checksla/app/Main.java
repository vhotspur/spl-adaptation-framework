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

package net.blabux.midigen.research;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class SineWave implements Iterable<Double> {
	private final static int MAX = 256;
	private final static Double[] VALUES;
	static {
		VALUES = new Double[MAX];
		double factor = 360.0 / MAX;
		IntStream.range(0, MAX).forEach((index) -> {
			double angle = index * factor;
			VALUES[index] = Math.sin(Math.toRadians(angle));
		});
	}

	public static List<Double> values() {
		return Arrays.asList(VALUES);
	}

	@Override
	public Iterator<Double> iterator() {
		return values().iterator();
	}

}

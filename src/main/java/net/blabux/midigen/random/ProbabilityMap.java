package net.blabux.midigen.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class ProbabilityMap<T> implements java.util.Iterator<T> {
	private final List<T> probs;
	private final Random random;

	public ProbabilityMap(Random random, Map<Integer, T> mapping) {
		int total = mapping.keySet().stream().reduce(Integer::sum).orElse(0);
		if (100 != total) {
			throw new IllegalArgumentException("Map keys must total 100");
		}
		this.random = random;
		probs = new ArrayList<T>(100);
		mapping.entrySet().forEach((entry) -> {
			int key = entry.getKey();
			T value = entry.getValue();
			IntStream.range(0, key).forEach((x) -> {
				probs.add(value);
			});
		});
	}
	
	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public T next() {
		return probs.get((int)(random.nextFloat() * 100));
	}
	
	
}

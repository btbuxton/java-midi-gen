package net.blabux.midigen.research.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class MergeIterable<T> implements Iterable<T> {
	Function<T,Long> sortValueGetter;
	Collection<Iterable<T>> iterables;
	
	public MergeIterable(Function<T,Long> sortValueGetter, Collection<Iterable<T>> iterables) {
		this.sortValueGetter = sortValueGetter;
		this.iterables = iterables;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new MergeIterator();
	}

	class MergeIterator implements Iterator<T> {
		final List<PeekableIterator<T>> iteratorsAndValues;
		Optional<T> current;

		public MergeIterator() {
			this.iteratorsAndValues = new ArrayList<>(iterables.size());
			for (Iterable<T> eachIterable : iterables) {
				this.iteratorsAndValues.add(new PeekableIterator<T>(eachIterable.iterator()));
			}
			calculateNextValues();
		}

		@Override
		public boolean hasNext() {
			return current.isPresent();
		}

		@Override
		public T next() {
			T result = current.get();
			calculateNextValues();
			return result;
		}

		private void calculateNextValues() {
			Optional<PeekableIterator<T>> minimum = Optional.empty();
			long minValue = Long.MAX_VALUE;
			for (PeekableIterator<T> each : iteratorsAndValues) {
				Optional<T> optionalValue = each.peek();
				if (optionalValue.isPresent()) {
					long tick = sortValueGetter.apply(optionalValue.get());
					if (tick < minValue) {
						minValue = tick;
						minimum = Optional.of(each);
					}
				}
			}
			if (minimum.isPresent()) {
				current = Optional.of(minimum.get().next());
			} else {
				current = Optional.empty();
			}
		}
	}
}

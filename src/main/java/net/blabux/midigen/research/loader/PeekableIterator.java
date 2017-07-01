package net.blabux.midigen.research.loader;

import java.util.Iterator;
import java.util.Optional;

public class PeekableIterator<T> implements Iterator<T> {
	final Iterator<T> iterator;
	Optional<T> next;

	public PeekableIterator(Iterator<T> iterator) {
		this.iterator = iterator;
		calculateNext();
	}

	public boolean hasNext() {
		return next.isPresent();
	}

	public T next() {
		T result = next.get();
		calculateNext();
		return result;
	}

	public Optional<T> peek() {
		return next;
	}

	private void calculateNext() {
		if (iterator.hasNext()) {
			next = Optional.of(iterator.next());
		} else {
			next = Optional.empty();
		}
	}
}

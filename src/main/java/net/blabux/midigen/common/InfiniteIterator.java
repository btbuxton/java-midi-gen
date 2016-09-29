package net.blabux.midigen.common;

import java.util.Iterator;

public class InfiniteIterator<E> implements Iterator<E> {
	private final Iterable<E> source;
	private Iterator<E> original;

	public InfiniteIterator(Iterable<E> source) {
		this.source = source;
	}
	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public E next() {
		if (null == original || !original.hasNext()) {
			original = source.iterator();
		}
		return original.next();
	}

}

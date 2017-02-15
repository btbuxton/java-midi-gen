package net.blabux.midigen.common;

import java.util.Iterator;

/**
 * Endless iterator. Works like ring in Sonic Pi
 * @author btbuxton
 *
 * @param <E>
 */
public class RingIterator<E> implements Iterator<E> {
	private final Iterable<E> source;
	private Iterator<E> original;

	public RingIterator(Iterable<E> source) {
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

package net.blabux.midigen.common;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This is NOT the endless sequence in SonicPi, as elements
 * are added, it's for keeping the collection at a constant max
 * size. It will grow until it reaches the length sent to the constructor
 */
public class LimitedIterable<T> implements Iterable<T> {
	final LinkedList<T> internal;
	final int length;
	int position;

	public LimitedIterable(int length) {
		this.length = length;
		this.internal = new LinkedList<T>();
		this.position = -1;
	}

	public void add(T item) {
		internal.addLast(item);
		if (internal.size() > length) {
			internal.removeFirst();
		}
	}

	public Iterator<T> iterator() {
		return internal.iterator();
	}
}

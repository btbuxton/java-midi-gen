package net.blabux.midigen.common;

import java.util.Iterator;
import java.util.LinkedList;

public class Ring<T> implements Iterable<T> {
	final LinkedList<T> internal;
	final int length;
	int position;

	public Ring(int length) {
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

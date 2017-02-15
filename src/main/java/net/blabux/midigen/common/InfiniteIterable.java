package net.blabux.midigen.common;

import java.util.Iterator;
import java.util.function.Supplier;

public class InfiniteIterable<T> implements Iterable<T>{
	private final Supplier<T> supplier;
	
	public InfiniteIterable(Supplier<T> supplier) {
		this.supplier = supplier;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private T next = supplier.get();
			@Override
			public boolean hasNext() {
				return null != next;
			}

			@Override
			public T next() {
				T result = next;
				next = supplier.get();
				return result;
			}
			
		};
	}

}

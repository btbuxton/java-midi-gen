package net.blabux.midigen.common;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import net.blabux.midigen.common.InfiniteIterable;

public class InfiniteIterableTest {

	@Test
	public void testInfinite() {
		Iterator<Integer> infinite = new InfiniteIterable<Integer>(() -> {
			return 0;
		}).iterator();
		assertTrue(infinite.hasNext());
		assertEquals((Integer)0, infinite.next());
		assertEquals((Integer)0, infinite.next());
		assertEquals((Integer)0, infinite.next());
		assertEquals((Integer)0, infinite.next());
	}

	@Test
	public void testFinite() {
		int[] intWrapper = new int[] { 0 };
		Iterator<Integer> finite = new InfiniteIterable<Integer>(() -> {
			if (3 == intWrapper[0]) {
				return null;
			}
			return intWrapper[0]++;
		}).iterator();
		assertTrue(finite.hasNext());
		assertEquals((Integer)0, finite.next());
		assertEquals((Integer)1, finite.next());
		assertEquals((Integer)2, finite.next());
		assertFalse(finite.hasNext());
	}
}

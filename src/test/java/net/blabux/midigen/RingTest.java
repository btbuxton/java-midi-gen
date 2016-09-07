package net.blabux.midigen;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import net.blabux.midigen.research.Ring;

public class RingTest {

	@Test
	public void testOne() {
		Ring<Integer> subject = new Ring<Integer>(3);
		subject.add(1);
		Iterator<Integer> iter = subject.iterator();
		assertTrue(iter.hasNext());
		assertEquals((Integer)1, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void testTwo() {
		Ring<Integer> subject = new Ring<Integer>(3);
		subject.add(1);
		subject.add(2);
		Iterator<Integer> iter = subject.iterator();
		assertTrue(iter.hasNext());
		assertEquals((Integer)1, iter.next());
		assertEquals((Integer)2, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void testThree() {
		Ring<Integer> subject = new Ring<Integer>(3);
		subject.add(1);
		subject.add(2);
		subject.add(3);
		Iterator<Integer> iter = subject.iterator();
		assertTrue(iter.hasNext());
		assertEquals((Integer)1, iter.next());
		assertEquals((Integer)2, iter.next());
		assertEquals((Integer)3, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void testFour() {
		Ring<Integer> subject = new Ring<Integer>(3);
		subject.add(1);
		subject.add(2);
		subject.add(3);
		subject.add(4);
		Iterator<Integer> iter = subject.iterator();
		assertTrue(iter.hasNext());
		assertEquals((Integer)2, iter.next());
		assertEquals((Integer)3, iter.next());
		assertEquals((Integer)4, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void testFive() {
		Ring<Integer> subject = new Ring<Integer>(3);
		subject.add(1);
		subject.add(2);
		subject.add(3);
		subject.add(4);
		subject.add(5);
		Iterator<Integer> iter = subject.iterator();
		assertTrue(iter.hasNext());
		assertEquals((Integer)3, iter.next());
		assertEquals((Integer)4, iter.next());
		assertEquals((Integer)5, iter.next());
		assertFalse(iter.hasNext());
	}

}

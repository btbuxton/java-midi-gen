package net.blabux.midigen.research;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class LFOIteratorTest {

	@Test
	public void test() {
		Iterator<Integer> lfo = new LFOIterator(4, 0.5, 64, 64);
		assertEquals((Integer)64, lfo.next());
		assertEquals((Integer)109, lfo.next());
		assertEquals((Integer)127, lfo.next());
		assertEquals((Integer)109, lfo.next());
		assertEquals((Integer)64, lfo.next());
		assertEquals((Integer)18, lfo.next());
		assertEquals((Integer)0, lfo.next());
		assertEquals((Integer)18, lfo.next());
	}

}

package net.blabux.midigen.midi.lfo;

import static org.junit.Assert.*;

import org.junit.Test;

public class LFOSquareTest {

	@Test
	public void testStartLow() {
		LFOSquare subject = new LFOSquare(4, 1.0, 64, 64, true);
		assertEquals((Integer)0, subject.next());
		assertEquals((Integer)0, subject.next());
		assertEquals((Integer)127, subject.next());
		assertEquals((Integer)127, subject.next());
		assertEquals((Integer)0, subject.next());
	}
	
	@Test
	public void testStartHigh() {
		LFOSquare subject = new LFOSquare(4, 1.0, 64, 64, false);
		assertEquals((Integer)127, subject.next());
		assertEquals((Integer)127, subject.next());
		assertEquals((Integer)0, subject.next());
		assertEquals((Integer)0, subject.next());
	}

}

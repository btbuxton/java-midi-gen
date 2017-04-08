package net.blabux.midigen.midi.lfo;

import static org.junit.Assert.*;

import org.junit.Test;

public class LFOSawTest {

	@Test
	public void testRise() {
		LFOSaw subject = new LFOSaw(4, 1.0, 64, 64, true);
		assertEquals((Integer)85, subject.next());
		assertEquals((Integer)127, subject.next());
		assertEquals((Integer)0, subject.next());
		assertEquals((Integer)43, subject.next());
		assertEquals((Integer)85, subject.next());
	}
	
	@Test
	public void testFall() {
		LFOSaw subject = new LFOSaw(4, 1.0, 64, 64, false);
		assertEquals((Integer)85, subject.next());
		assertEquals((Integer)43, subject.next());
		assertEquals((Integer)0, subject.next());
		assertEquals((Integer)127, subject.next());
		assertEquals((Integer)85, subject.next());
	}

}

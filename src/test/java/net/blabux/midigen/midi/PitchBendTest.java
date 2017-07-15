package net.blabux.midigen.midi;

import static org.junit.Assert.*;

import org.junit.Test;

public class PitchBendTest {

	@Test
	public void testA4Midi() {
		double midi = PitchBend.freqToMidi(440);
		assertEquals((int)6900, (int)(midi * 100));
	}
	
	@Test
	public void testA4Freq() {
		double freq = PitchBend.midiToFreq(69);
		assertEquals((int)44000, (int)(freq * 100));
	}
	
	@Test
	public void testMidi() {
		double midi = PitchBend.freqToMidi(8372.0180896192);
		assertEquals((int)12000, (int)(midi * 100));
	}
	
	@Test
	public void testFreq() {
		double freq = PitchBend.midiToFreq(120);
		assertEquals((int)8372018, (int)(freq * 1000));
	}

	@Test
	public void pitchBend() {
		double freq = 440 * 3 / 2; //perfect fifth
		PitchBend pb = new PitchBend(freq);
		assertEquals(76, pb.midiNote);
		assertEquals(8352, pb.amount);
	}
	
	@Test
	public void pitchBendMidi() {
		double freq = PitchBend.midiToFreq(78);
		PitchBend pb = new PitchBend(freq);
		assertEquals(78, pb.midiNote);
		assertEquals(8192, pb.amount);
	}
}

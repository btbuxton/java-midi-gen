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

	//http://www.elvenminstrel.com/music/tuning/reference/pitchbends.shtml
	@Test
	public void pitchBend() {
		double freq = 440.0 * 3 / 2; //perfect fifth
		PitchBend pb = new PitchBend(freq);
		assertEquals(76, pb.midiNote);
		assertEquals(8272, pb.amount);
		assertEquals(80, pb.amount - 8192);
	}
	
	@Test
	public void pitchBendAgain() {
		double freq = 440.0 * 5 / 3;
		PitchBend pb = new PitchBend(freq);
		assertEquals(78, pb.midiNote);
		assertEquals(7551, pb.amount);
		assertEquals(-641, pb.amount - 8192);
	}
	
	@Test
	public void pitchBendMidi() {
		double freq = PitchBend.midiToFreq(78);
		PitchBend pb = new PitchBend(freq);
		assertEquals(78, pb.midiNote);
		assertEquals(8192, pb.amount);
	}
	
	@Test
	public void getAmountForMidiPB() {
		double freq = 440 * 3 / 2; //perfect fifth
		PitchBend pb = new PitchBend(freq);
		byte[] amount = pb.getAmountForMidiPB();
		assertEquals(8272, pb.amount);
		assertEquals(0x50, amount[0]);
		assertEquals(0x40, amount[1]);
	}
	
	@Test
	public void getAmountForMidiPBCenter() {
		PitchBend pb = new PitchBend(440);
		byte[] amount = pb.getAmountForMidiPB();
		assertEquals(0x00, amount[0]);
		assertEquals(0x40, amount[1]);
	}
	
	@Test
	public void getAmountFor500() {
		PitchBend pb = new PitchBend(500);
		assertEquals(71, pb.midiNote);
		assertEquals(9065, pb.amount);
	}
	
	@Test
	public void getAmountFor510() {
		PitchBend pb = new PitchBend(510);
		assertEquals(72, pb.midiNote);
		assertEquals(6373, pb.amount);
	}
}

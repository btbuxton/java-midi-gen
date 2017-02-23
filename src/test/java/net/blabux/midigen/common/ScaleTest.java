package net.blabux.midigen.common;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import net.blabux.midigen.common.Scale.Mode;

public class ScaleTest {

	@Test
	public void testScale() {
		Note f2 = Note.BY_NAME.get("F2");
		List<Note> minor = Scale.MINOR_PENT.notes(f2);
		assertEquals(5, minor.size());
		assertEquals("F2", minor.get(0).toString());
		assertEquals("G#2", minor.get(1).toString());
		assertEquals("A#2", minor.get(2).toString());
		assertEquals("C3", minor.get(3).toString());
		assertEquals("D#3", minor.get(4).toString());
	}
	
	@Test
	public void testMode() {
		Note e2 = Note.BY_NAME.get("E2");
		List<Note> phrygian = Scale.MAJOR.mode(Mode.Phrygian).notes(e2);
		assertEquals(7, phrygian.size());
		assertEquals("E2", phrygian.get(0).toString());
		assertEquals("F2", phrygian.get(1).toString());
		assertEquals("G2", phrygian.get(2).toString());
		assertEquals("A2", phrygian.get(3).toString());
		assertEquals("B2", phrygian.get(4).toString());
		assertEquals("C3", phrygian.get(5).toString());
		assertEquals("D3", phrygian.get(6).toString());
	}

}

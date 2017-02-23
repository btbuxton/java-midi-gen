package net.blabux.midigen.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import net.blabux.midigen.common.Note;

public class NoteTest {

	@Test
	public void testMiddleCByValue() {
		Note middleC = Note.ALL.get(60);
		assertEquals("C3", middleC.getName());
	}

	@Test
	public void testMiddleCByName() {
		Note middleC = Note.BY_NAME.get("C3");
		assertEquals("C3", middleC.getName());
	}

	@Test
	public void testScale() {
		Note f2 = Note.BY_NAME.get("F2");
		List<Note> minor = f2.scale(Note.MINOR_PENT);
		assertEquals(5, minor.size());
		assertEquals("F2", minor.get(0).getName());
		assertEquals("G#2", minor.get(1).getName());
		assertEquals("A#2", minor.get(2).getName());
		assertEquals("C3", minor.get(3).getName());
		assertEquals("D#3", minor.get(4).getName());
	}
	
	@Test
	public void testMode() {
		Note e2 = Note.BY_NAME.get("E2");
		List<Note> phrygian = e2.scale(Note.mode(Note.MAJOR, 3));
		assertEquals(7, phrygian.size());
		assertEquals("E2", phrygian.get(0).getName());
		assertEquals("F2", phrygian.get(1).getName());
		assertEquals("G2", phrygian.get(2).getName());
		assertEquals("A2", phrygian.get(3).getName());
		assertEquals("B2", phrygian.get(4).getName());
		assertEquals("C3", phrygian.get(5).getName());
		assertEquals("D3", phrygian.get(6).getName());
	}
	
	@Test
	public void testOctaveUp() {
		Note e2 = Note.BY_NAME.get("E2");
		assertEquals("E3", e2.octaveUp().getName());
	}
	
	@Test
	public void testOctaveDown() {
		Note e2 = Note.BY_NAME.get("E2");
		assertEquals("E1", e2.octaveDown().getName());
	}

}

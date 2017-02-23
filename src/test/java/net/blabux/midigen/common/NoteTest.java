package net.blabux.midigen.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NoteTest {

	@Test
	public void testMiddleCByValue() {
		Note middleC = Note.ALL.get(60);
		assertEquals("C3", middleC.toString());
	}

	@Test
	public void testMiddleCByName() {
		Note middleC = Note.BY_NAME.get("C3");
		assertEquals("C", middleC.getName());
		assertEquals(3, middleC.getOctave());
		assertEquals("C3", middleC.toString());
	}
	
	@Test
	public void testOctaveUp() {
		Note e2 = Note.BY_NAME.get("E2");
		assertEquals("E3", e2.octaveUp().toString());
	}
	
	@Test
	public void testOctaveDown() {
		Note e2 = Note.BY_NAME.get("E2");
		assertEquals("E1", e2.octaveDown().toString());
	}

}

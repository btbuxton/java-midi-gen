package net.blabux.midigen;

import static org.junit.Assert.*;

import org.junit.Test;

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

}

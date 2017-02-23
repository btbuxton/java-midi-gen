package net.blabux.midigen.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class NoteNameTest {

	@Test
	public void get() {
		NoteName c = NoteName.get("B#");
		assertEquals("C", c.name());
	}
	
	@Test
	public void name() {
		NoteName c = NoteName.get("C");
		assertEquals("C", c.name());
	}
	
	@Test
	public void ordinal() {
		NoteName c = NoteName.C;
		assertEquals(0, c.ordinal());
		NoteName b = NoteName.B;
		assertEquals(11, b.ordinal());
	}

}

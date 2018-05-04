package net.blabux.midigen.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void note() {
        Note c2 = NoteName.C.note(2);
        assertEquals("C2", c2.toString());
    }

}

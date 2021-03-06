package net.blabux.midigen.midi.lfo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LFOTriangleTest {

    @Test
    public void testSimple() {
        LFO subject = new LFOImpl(new Triangle(),4, 1.0, 64, 64);
        assertEquals((Integer) 64, subject.next());
        assertEquals((Integer) 127, subject.next());
        assertEquals((Integer) 64, subject.next());
        assertEquals((Integer) 0, subject.next());
        assertEquals((Integer) 64, subject.next());
    }

}

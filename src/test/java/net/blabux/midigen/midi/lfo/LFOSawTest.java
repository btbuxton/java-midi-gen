package net.blabux.midigen.midi.lfo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LFOSawTest {

    @Test
    public void testRise() {
        LFO subject = new LFOImpl(new Saw(),4, 1.0, 64, 64);
        assertEquals((Integer) 64, subject.next());
        assertEquals((Integer) 96, subject.next());
        assertEquals((Integer) 0, subject.next());
        assertEquals((Integer) 32, subject.next());
        assertEquals((Integer) 64, subject.next());
    }

}

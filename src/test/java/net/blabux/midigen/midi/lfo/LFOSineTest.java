package net.blabux.midigen.midi.lfo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LFOSineTest {

    @Test
    public void testOneCyclePerQuarterNote() {
        LFO lfo = new LFOImpl(new Sine(), 4, 1.0, 64, 64);
        assertEquals((Integer) 64, lfo.next());
        assertEquals((Integer) 127, lfo.next());
        assertEquals((Integer) 64, lfo.next());
        assertEquals((Integer) 0, lfo.next());
        assertEquals((Integer) 64, lfo.next());
    }

    @Test
    public void testOneCyclesPerHalfNote() {
        LFO lfo = new LFOImpl(new Sine(),4, 0.5, 64, 64);
        assertEquals((Integer) 64, lfo.next());
        assertEquals((Integer) 109, lfo.next());
        assertEquals((Integer) 127, lfo.next());
        assertEquals((Integer) 109, lfo.next());
        assertEquals((Integer) 64, lfo.next());
    }

    @Test
    public void testOneCyclesPerQuarterNoteEightPulses() {
        LFO lfo = new LFOImpl(new Sine(),8, 1.0, 64, 64);
        assertEquals((Integer) 64, lfo.next());
        assertEquals((Integer) 109, lfo.next());
        assertEquals((Integer) 127, lfo.next());
        assertEquals((Integer) 109, lfo.next());
        assertEquals((Integer) 64, lfo.next());
        assertEquals((Integer) 19, lfo.next());
        assertEquals((Integer) 0, lfo.next());
        assertEquals((Integer) 19, lfo.next());
        assertEquals((Integer) 64, lfo.next());
    }

}

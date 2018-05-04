package net.blabux.midigen.midi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MidiMathTest {
    @Test
    public void testA4Midi() {
        double midi = MidiMath.freqToMidi(440);
        assertEquals((int) 6900, (int) (midi * 100));
    }

    @Test
    public void testA4Freq() {
        double freq = MidiMath.midiToFreq(69);
        assertEquals((int) 44000, (int) (freq * 100));
    }

    @Test
    public void testMidi() {
        double midi = MidiMath.freqToMidi(8372.0180896192);
        assertEquals((int) 12000, (int) (midi * 100));
    }

    @Test
    public void testFreq() {
        double freq = MidiMath.midiToFreq(120);
        assertEquals((int) 8372018, (int) (freq * 1000));
    }

}

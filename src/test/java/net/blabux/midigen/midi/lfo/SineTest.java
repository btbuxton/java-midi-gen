package net.blabux.midigen.midi.lfo;

import org.junit.Test;

import static org.junit.Assert.*;

public class SineTest {
    @Test
    public void testSimple() {
        Sine sine = new Sine();
        assertEquals(0.0, sine.value(0), 0.00001);
        assertEquals(0.7071, sine.value(Math.toRadians(45)), 0.00001);
        assertEquals(1.0, sine.value(Math.toRadians(90)), 0.00001);
        assertEquals(0.0, sine.value(Math.toRadians(180)), 0.00001);
        assertEquals(-1.0, sine.value(Math.toRadians(270)), 0.00001);
    }
}

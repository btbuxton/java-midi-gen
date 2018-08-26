package net.blabux.midigen.midi.lfo;


public class Sine implements Wave {
    /**
     * Returns -1 to 1
     */
    public double value(double pos) {
        return Math.sin(pos);
    }
}

package net.blabux.midigen.midi.lfo;

public interface Wave {
    /**
     * pos is a float that denotes the position in the cycle (0 <= pos < 2*pi)
     * Return a value of -1 to 1, shoud start at 0 for pos 0
     * @param pos
     * @return
     */
    double value(double pos);
}

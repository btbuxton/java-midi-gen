package net.blabux.midigen.midi.lfo;

public abstract class WaveAbstract implements Wave {
    final static double CYCLE_RADS = Math.toRadians(360);

    double normalizeRadians(double pos) {
        double multiple = pos / CYCLE_RADS;
        return (multiple - (int)multiple) * CYCLE_RADS;
    }
}

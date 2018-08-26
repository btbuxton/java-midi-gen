package net.blabux.midigen.midi.lfo;

public class LFOImpl implements LFO {
    final double radStep;
    final int center;
    final int depth;
    final Wave wave;
    double phase;

    public LFOImpl(Wave wave, long ppq, double cpq, int center, int depth) {
        this.wave = wave;
        this.center = center;
        this.depth = depth;
        double degStep = 360.0 * cpq / ppq;
        radStep = Math.toRadians(degStep);
        phase = Math.toRadians(0);
    }

    @Override
    public Integer next() {
        int value = (int) Math.round(center + (depth * wave.value(phase)));
        phase += radStep;
        return scrub(value);
    }


    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Ensure the input integer is between 0-127
     *
     * @param input
     * @return
     */
    int scrub(int input) {
        return Math.min(127, Math.max(0, input));
    }
//    int scrub(double input) {
//        return scrub((int) Math.round(input));
//    }
}

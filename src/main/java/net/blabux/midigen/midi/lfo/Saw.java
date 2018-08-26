package net.blabux.midigen.midi.lfo;

public class Saw extends WaveAbstract {
    private final static double FIRST = Math.toRadians(180);

    /*
     * (x,y) -> (deg,val)
     * (0,0) -> (180,1) -> (180,-1) -> (360,0)
     * (0,0) -> (180,1) m = 1 / 180, b=
     * (180,-1) -> (360,0) m= 1 / 180, b=-2
     */
    @Override
    public double value(double pos) {
        double normPos = normalizeRadians(pos);

        if (normPos < FIRST) {
            return (1 / FIRST) * normPos;
        }
        return ((1 / FIRST) * normPos) - 2;
    }
}

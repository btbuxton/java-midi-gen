package net.blabux.midigen.midi.lfo;

public class Triangle extends WaveAbstract {
    private final static double FIRST = Math.toRadians(90);
    private final static double SECOND = Math.toRadians(270);
    /*
     * (x,y) -> (deg,val)
     * (0,0) -> (90,1) -> (180,0) -> (270,-1) -> (360,0)
     * (0,0) -> (90,1) -> (270,-1) -> (360,0)
     *
     * First quarter:
     * (0,0) -> (90,1) m = y/x = 1/90, b=0
     *
     * half:
     * (90,1) -> (270,-1) = y/x = (-1 -1 / 270 - 90) = -2 / 180 = -1/90, b=2
     *
     * last:
     * (270,-1)->(360,0) = y/x = (0 + 1) / 360 - 270 = 1/90, b=-4
     */
    @Override
    public double value(double pos) {
        double normPos = normalizeRadians(pos);
        if (normPos < FIRST) {
            return (1 / FIRST) * normPos;
        }
        if (normPos < SECOND) {
            return 2 - ((1 / FIRST) * normPos);
        }
        return ((1 / FIRST) * normPos) - 4;
    }
}

package net.blabux.midigen.midi.lfo;

public class Square implements Wave {
    final Wave sine;

    public Square() {
        sine = new Sine();
    }
    @Override
    public double value(double pos) {
        double sineValue = sine.value(pos);
        if (sineValue < 0) {
            return -1.0;
        } else if (sineValue > 0){
            return 1.0;
        }
        return 0.0;
    }
}

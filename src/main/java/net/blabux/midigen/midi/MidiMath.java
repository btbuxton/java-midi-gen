package net.blabux.midigen.midi;

/**
 * Placeholder for now, would like to move to custom classes later
 *
 *  https://www.midikits.net/midi_analyser/midi_note_frequency.htm
 *  Hertz = 440.0 * pow(2.0, (midi note - 69)/12)
 *  midi note = log(Hertz/440.0)/log(2) * 12 + 69
 */
public class MidiMath {
    private static final int DEFAULT_BASE = 440;

    public static double freqToMidi(double freq) {
        return freqToMidi(freq, DEFAULT_BASE);
    }

    public static double freqToMidi(double freq, double base) {
        return Math.log(freq / base) / Math.log(2) * 12 + 69;
    }

    public static double midiToFreq(double midi) {
        return midiToFreq(midi, 440);
    }

    public static double midiToFreq(double midi, double base) {
        return base * Math.pow(2.0, (midi - 69) / 12.0);
    }
}

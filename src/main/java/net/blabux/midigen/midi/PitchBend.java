package net.blabux.midigen.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.util.function.Consumer;

/**
 * http://www.midikits.net/midi_analyser/pitch_bend.htm
 * https://music.stackexchange.com/questions/47995/midi-pitch-bend-vs-midi-tuning-standard-microtones
 * https://forum.juce.com/t/mapping-frequencies-to-midi-notes/1762/2
 * <p>
 * Assumes +/- 2 semitones for pitch bend
 *
 * @author btbuxton
 */
public class PitchBend {
    private static final int DEFAULT_BASE = 440;
    final int midiNote;
    final int amount;


    public PitchBend(double freq) {
        this(freq, DEFAULT_BASE);
    }

    public PitchBend(double freq, double base) {
        double equalTempNote = MidiMath.freqToMidi(freq, base);
        midiNote = (int) Math.round(equalTempNote);
        double freqMidiNote = MidiMath.midiToFreq(midiNote, base);
        //The 2 in (8192/2) is what determines +/- 2 semitones - that's my hypothesis
        amount = (int) Math.round(8192 + (8192 / 2) * 12 * Math.log(freq / freqMidiNote) / Math.log(2));
    }

    byte[] getAmountForMidiPB() {
        byte[] result = new byte[2];
        result[0] = (byte) (amount & 0x7F);
        result[1] = (byte) ((amount >> 7) & 0x7F);
        return result;
    }

    public void setToWholeToneBend(Consumer<MidiEvent> consumer, int channel, long tick) throws InvalidMidiDataException {
        //Bn 65 00 ; 101  00 MSB
        //Bn 64 00 ; 100 00 LSB
        //Bn 06 18 ; 06 24 MSB
        //Bn 26 00 ; 38 00 LSB
        consumer.accept(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0x65, 0x00), tick++));
        consumer.accept(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0x64, 0x00), tick++));
        consumer.accept(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0x06, 0x04), tick++));
        consumer.accept(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0x26, 0x00), tick++));

    }
}

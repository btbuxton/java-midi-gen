package net.blabux.midigen.midi;

import java.util.function.Consumer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * http://www.midikits.net/midi_analyser/pitch_bend.htm
 * https://music.stackexchange.com/questions/47995/midi-pitch-bend-vs-midi-tuning-standard-microtones
 * https://forum.juce.com/t/mapping-frequencies-to-midi-notes/1762/2
 * 
 * https://www.midikits.net/midi_analyser/midi_note_frequency.htm
 * Hertz = 440.0 * pow(2.0, (midi note - 69)/12)
 * midi note = log(Hertz/440.0)/log(2) * 12 + 69
 * @author btbuxton
 *
 */
public class PitchBend {
	final int midiNote;
	final int amount;
	
	static double freqToMidi(double freq) {
		return Math.log(freq / 440.0)/Math.log(2) * 12 + 69;
	}
	static double midiToFreq(double midi) {
		return 440.0 * Math.pow(2.0, (midi - 69) / 12.0);
	}
	
	public PitchBend(double freq) {
		double equalTempNote = freqToMidi(freq);
		midiNote = (int)Math.round(equalTempNote);
		double decimal = equalTempNote - midiNote;
		amount = (int)((decimal * 8192) + 8192) % 16384;
	}
	
	byte[] getAmountForMidiPB() {
		byte[] result = new byte[2];
		result[0] = (byte)(amount & 0x7F);
		result[1] = (byte)((amount >> 7) & 0x7F);
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

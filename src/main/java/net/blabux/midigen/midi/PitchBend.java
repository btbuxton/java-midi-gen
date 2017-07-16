package net.blabux.midigen.midi;
/**
 * http://www.midikits.net/midi_analyser/pitch_bend.htm
 * https://music.stackexchange.com/questions/47995/midi-pitch-bend-vs-midi-tuning-standard-microtones
 * https://forum.juce.com/t/mapping-frequencies-to-midi-notes/1762/2
 * 
 * https://www.midikits.net/midi_analyser/midi_note_frequency.htm
 * Hertz = 440.0 * pow(2.0, (midi note - 69)/12)
 * midi note = log(Hertz/440.0)/log(2) * 12 + 69
 * 
 * Assumes +/- 2 semitones for pitch bend
 * @author btbuxton
 *
 */
public class PitchBend {
	private static final int DEFAULT_BASE = 440;
	final int midiNote;
	final int amount;
	
	static double freqToMidi(double freq) {
		return freqToMidi(freq, DEFAULT_BASE);
	}
	
	static double freqToMidi(double freq, double base) {
		return Math.log(freq / base)/Math.log(2) * 12 + 69;
	}
	
	static double midiToFreq(double midi) {
		return midiToFreq(midi, 440);
	}
	
	static double midiToFreq(double midi, double base) {
		return base * Math.pow(2.0, (midi - 69) / 12.0);
	}
	
	public PitchBend(double freq) {
		this(freq, DEFAULT_BASE);
	}
	
	public PitchBend(double freq, double base) {
		double equalTempNote = freqToMidi(freq, base);
		midiNote = (int)Math.round(equalTempNote);
		double freqMidiNote = midiToFreq(midiNote, base);
		//The 2 in (8192/2) is what determines +/- 2 semitones - that's my hypothesis
		amount = (int)Math.round(8192 + (8192/2) * 12 * Math.log(freq / freqMidiNote)/Math.log(2));
	}
	
	byte[] getAmountForMidiPB() {
		byte[] result = new byte[2];
		result[0] = (byte)(amount & 0x7F);
		result[1] = (byte)((amount >> 7) & 0x7F);
		return result;
	}
}

package net.blabux.midigen.research;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import net.blabux.midigen.common.Note;

public class TrackBuilder {
	private final Track track;
	private final int channel;

	public TrackBuilder(Sequence seq, int channel) {
		this.track = seq.createTrack();
		this.channel = channel;
	}
	
	public void note(Note note, long when, long howLong) throws InvalidMidiDataException {
		noteOn(note, when);
		noteOff(note, when + howLong);
	}
	
	public void noteOn(Note note, long when) throws InvalidMidiDataException {
		MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, channel, note.getValue(), 100);
		MidiEvent eventOn = new MidiEvent(msgOn, when);
		track.add(eventOn);
	}
	
	public void noteOff(Note note, long when) throws InvalidMidiDataException {
		MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, note.getValue(), 0);
		MidiEvent eventOff = new MidiEvent(msgOff, when);
		track.add(eventOff);
	}

	public void placebo(long when) throws InvalidMidiDataException {
		MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, 0, 0);
		MidiEvent eventOff = new MidiEvent(msgOff, when);
		track.add(eventOff);
	}
	
	public Track build() {
		return track;
	}
}

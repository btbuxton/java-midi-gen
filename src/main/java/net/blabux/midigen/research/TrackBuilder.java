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
	
	public void note(long when, Note note, long howLong) throws InvalidMidiDataException {
		noteOn(when, note);
		noteOff(when + howLong, note);
	}
	
	public void noteOn(long when, Note note) throws InvalidMidiDataException {
		MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, channel, note.getValue(), 100);
		add(when, msgOn);
	}
	
	public void noteOff(long when, Note note) throws InvalidMidiDataException {
		MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, note.getValue(), 0);
		add(when, msgOff);
	}
	
	public void cc(long when, int controlNumber, int value) throws InvalidMidiDataException {
		MidiMessage msgCC = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controlNumber, value);
		add(when, msgCC);
	}

	public void placebo(long when) throws InvalidMidiDataException {
		MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, 0, 0);
		add(when, msgOff);
	}

	private void add(long when, MidiMessage msg) {
		MidiEvent event = new MidiEvent(msg, when);
		track.add(event);
	}
	
	public Track build() {
		return track;
	}
}

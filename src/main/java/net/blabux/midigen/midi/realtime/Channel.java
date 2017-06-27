package net.blabux.midigen.midi.realtime;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import net.blabux.midigen.common.Note;

/**
 * Encapsulate actions for a channel.
 * It's also so everyone doesn't have to put try blocks around calls to midi
 * @author btbuxton
 *
 */
public class Channel {
	private final Receiver midiRecv;
	private final int id;
	
	public Channel(Receiver midiRecv, int id) {
		if (id < 0 || id > 15) {
			throw new IllegalArgumentException("Channel id must be between 0-15, got: " + String.valueOf(id));
		}
		this.midiRecv = midiRecv;
		this.id = id;
	}
	
	public void noteOn(Note note, int velocity) {
		try {
			MidiMessage noteOn = new ShortMessage(ShortMessage.NOTE_ON, id, note.getValue(), velocity);
			midiRecv.send(noteOn, -1);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void noteOff(Note note) {
		try {
			MidiMessage noteOff = new ShortMessage(ShortMessage.NOTE_OFF, id, note.getValue(), 0);
			midiRecv.send(noteOff, -1);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	public void cc(int cc, int nextValue) {
		try {
			MidiMessage ccEvent = new ShortMessage(ShortMessage.CONTROL_CHANGE, cc, nextValue);
			midiRecv.send(ccEvent, -1);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}
	
}

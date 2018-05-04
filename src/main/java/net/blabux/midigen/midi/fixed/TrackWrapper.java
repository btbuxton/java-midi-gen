package net.blabux.midigen.midi.fixed;

import net.blabux.midigen.common.Note;

import javax.sound.midi.*;

public class TrackWrapper {
    private final Track track;
    private final int channel;

    public TrackWrapper(Sequence seq, int channel) {
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

package net.blabux.midigen.research.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

import net.blabux.midigen.common.Note;

public class CurrentState {
	private final List<Map<Note,Integer>> playing;
	private long length;
	private int ppq;
	private double bpm;
	private long tick;
	
	public CurrentState() {
		this.playing = new ArrayList<>(16);
		for (int i=0; i < 16; i++) {
			this.playing.add(new HashMap<>());
		}
	}
	
	public double lengthMinutes() {
		return length / (bpm * ppq);
	}
	
	public long getLength() {
		return length;
	}

	public int getPPQ() {
		return ppq;
	}

	public double getBPM() {
		return bpm;
	}
	
	public long getTick() {
		return tick;
	}
	
	public boolean hasAnyNotes() {
		return playing.stream().anyMatch((notes)->!notes.isEmpty());
	}

	public List<Map<Note,Integer>> getPlaying() {
		return playing;
	}
	
	public List<Note> getAllNotes() {
		return playing.stream().flatMap((each) -> each.keySet().stream()).collect(Collectors.toList());
	}
	
	public List<Map.Entry<Note,Integer>> getAllNotesAndVelocities() {
		return playing.stream().flatMap((each) -> each.entrySet().stream()).collect(Collectors.toList());
	}


	void setLength(long tickLength) {
		this.length = tickLength;
	}

	void setPPQ(int ppq) {
		this.ppq = ppq;
	}

	void setBPM(double tempo) {
		this.bpm = tempo;
	}
	
	void noteOff(int channel, long tick, int note, int velocity) {
		playing.get(channel).remove(Note.ALL.get(note));
	}

	void noteOn(int channel, long tick, int note, int velocity) {
		if (0 == velocity) {
			noteOff(channel, tick, note, velocity);
			return;
		}
		playing.get(channel).put(Note.ALL.get(note), velocity);
	}
	
	void update(MidiEvent evt) {
		this.tick = evt.getTick();
		final MidiMessage msg = evt.getMessage();
		final int status = msg.getStatus() & 0xF0;
		if (status == 0x90) {
			byte[] raw = msg.getMessage();
			noteOn(msg.getStatus() & 0x0F, tick, raw[1] & 0x7F, raw[2] & 0x7F);
		} else if (status == 0x80) {
			byte[] raw = msg.getMessage();
			noteOff(msg.getStatus() & 0x0F, tick, raw[1] & 0x7F, raw[2] & 0x7F);
		} else if (msg.getStatus() == 0xFF) {
			byte[] raw = msg.getMessage();
			if (0x51 == raw[1] && 0x03 == raw[2]) {
				long mspb = ((long) raw[3] << 16) + ((long) raw[4] << 8) + (long) raw[5];
				double tempo = 60_000_000.0 / mspb;
				setBPM(tempo);
			}
		}
	}

}

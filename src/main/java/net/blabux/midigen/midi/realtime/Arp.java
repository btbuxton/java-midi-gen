package net.blabux.midigen.midi.realtime;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import net.blabux.midigen.common.Note;
import net.blabux.midigen.common.RingIterator;
import net.blabux.midigen.midi.MidiUtil;

public class Arp {
	private final RingIterator<Note> ring;
	private final double noteLength;
	private final double gateLength;

	private long nextTickOn;
	private long nextTickOff;
	private Note currentNote;

	public Arp(List<Note> notes, double noteLengthInBeats, double gatePercentage) {
		this.ring = new RingIterator<>(notes);
		this.noteLength = noteLengthInBeats;
		this.gateLength = noteLengthInBeats * gatePercentage;
		this.nextTickOn = 0;
		this.nextTickOff = Long.MAX_VALUE;
	}

	public void tick(Receiver recv, Pulse pulse, long tick) {
		try {
			if (tick >= nextTickOn) {
				currentNote = ring.next();
				final MidiMessage noteOn = new ShortMessage(ShortMessage.NOTE_ON, 0, currentNote.getValue(), 100);
				recv.send(noteOn, -1);
				nextTickOff = tick + pulse.ticks(gateLength) - 1;
				nextTickOn = tick + pulse.ticks(noteLength) - 1;
			} else if (tick >= nextTickOff) {
				final MidiMessage noteOn = new ShortMessage(ShortMessage.NOTE_OFF, 0, currentNote.getValue(), 0);
				recv.send(noteOn, -1);
				nextTickOff = Long.MAX_VALUE;
			}
		} catch (InvalidMidiDataException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void main(String[] args) {
		Arp arp = defaultArp();
		try {
			final MidiDevice device = MidiUtil.getMidiReceiversContainingNameOrDefault(System.getProperty("recv", ""));
			device.open();
			try {
				System.out.println("Using: " + device.getDeviceInfo().getName());
				try (final Receiver recv = device.getReceiver()) {
					final Pulse pulse = new Pulse(120);
					final long one_min = pulse.ticks(120);
					Function<Long, Boolean> pulseAccept = (tick) -> {
						sendClock(recv);
						arp.tick(recv, pulse, tick);
						return tick < one_min;
					};
					try {
						pulse.run(pulseAccept);
					} finally {
						allNotesOff(recv);
					}
				}
			} finally {
				device.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Done!");
	}

	private static void sendClock(final Receiver recv) {
		try {
			MidiMessage msg = new ShortMessage(ShortMessage.TIMING_CLOCK);
			recv.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}

	}

	private static void allNotesOff(final Receiver recv) {
		try {
			MidiMessage msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, 123, 0);
			recv.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}

	}

	private static Arp defaultArp() {
		List<Note> notes = Arrays.asList(Note.named("G3"), Note.named("B3"), Note.named("D4"));
		return new Arp(notes, 0.5, 0.5); // straight 1/8th notes gated to be
											// 1/16th
	}
}

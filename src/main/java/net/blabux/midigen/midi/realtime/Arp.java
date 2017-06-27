package net.blabux.midigen.midi.realtime;

import java.util.Arrays;
import java.util.List;

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

	public boolean tick(Receiver recv, PulseGen pulse, long tick) {
		try {
			if (tick >= nextTickOn) {
				currentNote = ring.next();
				final MidiMessage noteOn = new ShortMessage(ShortMessage.NOTE_ON, 0, currentNote.getValue(), 100);
				recv.send(noteOn, -1);
				nextTickOff = tick + pulse.ticks(gateLength) - 1;
				nextTickOn = tick + pulse.ticks(noteLength);
				System.out.println("note on");
			} else if (tick >= nextTickOff) {
				final MidiMessage noteOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, currentNote.getValue(), 0);
				recv.send(noteOff, -1);
				nextTickOff = Long.MAX_VALUE;
				System.out.println("note off");
				return false;
			}
		} catch (InvalidMidiDataException ex) {
			throw new RuntimeException(ex);
		}
		return true;
	}

	public static void main(String[] args) {
		Arp arp = defaultArp();
		try {
			MidiUtil.getMidiReceiverNames().forEach(System.out::println);
			final MidiDevice device = MidiUtil.getMidiReceiversContainingNameOrDefault(System.getProperty("recv", ""));
			System.out.println("Using: " + device.getDeviceInfo().getName());
			device.open();
			try {
				try (final Receiver recv = device.getReceiver()) {
					final PulseGen pulse = new PulseGen(120, 240);
					final long one_min = pulse.ticks(120);
					PulseFunc pulseAccept = (tick) -> {
						return arp.tick(recv, pulse, tick) || tick < one_min;
					};
					final Clock clock = new Clock(recv, pulse);
					try {
						pulse.run(clock.andThen(pulseAccept));
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

	/**
	 * This doesn't work....grrrr
	 * @param recv
	 */
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

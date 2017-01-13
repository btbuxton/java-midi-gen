package net.blabux.midigen.example;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import net.blabux.midigen.common.Note;
import net.blabux.midigen.midi.MidiUtil;
/**
 * Simple testing of the framework
 * This is meant to be a playground only
 * 
 * Things will be commented in/out for my purposes only
 * @author btbuxton
 *
 */
public class Main {

	public static void main(String[] args) {
		Main main = new Main();
		try {
			main.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void run() throws MidiUnavailableException, InvalidMidiDataException, RuntimeException {
		Optional<MidiDevice> toUse = MidiUtil.getMidiReceivers()
				.findFirst();
		playSimpleSequence(toUse.orElseThrow(()-> new RuntimeException("No Midi Receiver Available")));
	}

	private void playSimpleSequence(MidiDevice toUse) throws MidiUnavailableException, InvalidMidiDataException {
		Sequence seq = new Sequence(Sequence.PPQ, 24);
		Track track = seq.createTrack();
		List<Note> notes = Note.BY_NAME.get("D2").scale(Note.MINOR_PENT);
		long seed = System.nanoTime();
		Random random = new Random(seed);
		Collections.shuffle(notes, random);
		addNotes(track, notes, 24, 12);
		notes = Note.BY_NAME.get("D2").scale(Note.MINOR_PENT); //D3
		Collections.shuffle(notes, random);
		addNotes(track, notes, 16, 18);
		notes = Note.BY_NAME.get("D2").scale(Note.MINOR_PENT); //D1
		Collections.shuffle(notes, random);
		addNotes(track, notes, 72, 4);
		System.out.println("seq tick length: " + seq.getTickLength());
		toUse.open();
		try {
			Receiver rec = toUse.getReceiver();
			try {
				Sequencer seqr = MidiSystem.getSequencer(false);
				seqr.setSequence(seq);
				seqr.setTempoInBPM(60.0f);
				seqr.getTransmitter().setReceiver(rec);
				seqr.open();
				seqr.setLoopCount(3); // Sequencer.LOOP_CONTINUOUSLY
				seqr.setLoopEndPoint(-1);
				addMetaEventListener(seqr);
				System.out.println("loop start: " + seqr.getLoopStartPoint());
				System.out.println("loop end: " + seqr.getLoopEndPoint());
				System.out.println("sequencer ticks: " + seqr.getTickLength());
				try {
					seqr.start();
					while (seqr.isRunning()) {
						sleep(200);
					}
				} finally {
					seqr.close();
				}

			} finally {
				rec.close();
			}
		} finally {
			toUse.close();
		}

	}

	private void addNotes(Track track, List<Note> notes, int note_length, int repeats) throws InvalidMidiDataException {
		int ticks = 0;
		for (int i = 0; i < repeats; i++) {
			for (Note each : notes) {
				MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, 0, each.getValue(), 100);
				MidiEvent eventOn = new MidiEvent(msgOn, ticks);
				track.add(eventOn);
				MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, each.getValue(), 0);
				MidiEvent eventOff = new MidiEvent(msgOff, (int) (ticks * 0.75));
				track.add(eventOff);
				eventOff = new MidiEvent(msgOff, ticks + note_length);
				track.add(eventOff);
				ticks += note_length;
			}
		}
	}

	private void addMetaEventListener(Sequencer seqr) {
		// 01 <len> <text> Text Event
		int endOftrackMessage = 47; // 0x2F
		seqr.addMetaEventListener(new MetaEventListener() {

			@Override
			public void meta(MetaMessage meta) {
				if (endOftrackMessage == meta.getType()) {
					Main.this.notifyAll();
				}
			}
		});

	}

	private void sleep(long ms) {
		synchronized (this) {
			try {
				wait(ms);
			} catch (InterruptedException e) {
				// IGNORE IT
			}
		}
	}

}

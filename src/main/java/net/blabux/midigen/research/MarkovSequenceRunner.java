package net.blabux.midigen.research;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

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
import javax.sound.midi.MidiDevice.Info;

import net.blabux.midigen.InfiniteIterator;
import net.blabux.midigen.Note;
import net.blabux.midigen.RhythmGenerator;

public class MarkovSequenceRunner {
	private static final int PPQ = 24;
	private static final int END_OF_TRACK = 0x2F;

	public static void main(String[] args) {
		MarkovSequenceRunner runner = new MarkovSequenceRunner();
		try {
			runner.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void run() throws Exception {
		URL url = getClass().getResource("/RunningLate-DanWheeler.mid");
		Chain chain = new ChainLoader(16).loadChain(url);
		Iterator<Note> notes = new InfiniteIterator<>(chain);
		MidiDevice device = getMidiDevice();
		device.open();
		try {
			Supplier<Sequence> seqSupplier = () -> {
				try {
					return createSequence(notes);
				} catch (InvalidMidiDataException e) {
					throw new RuntimeException(e);
				}
			};
			play(device, seqSupplier);
		} finally {
			device.close();
		}
	}

	private MidiDevice getMidiDevice() throws MidiUnavailableException {
		MidiDevice first = null;
		for (MidiDevice receiver : getReceivers()) {
			if (null == first) {
				first = receiver;
			}
			if (receiver.getDeviceInfo().getName().startsWith("Boutiq")) {
				return receiver;
			}
		}
		return first;
	}

	private List<MidiDevice> getReceivers() throws MidiUnavailableException {
		List<MidiDevice> result = new ArrayList<>();
		Info[] devices = MidiSystem.getMidiDeviceInfo();
		for (Info each : devices) {
			MidiDevice device = MidiSystem.getMidiDevice(each);
			if (0 != device.getMaxReceivers()) {
				result.add(device);
			}
		}
		return result;
	}

	private Sequence createSequence(Iterator<Note> allNotes) throws InvalidMidiDataException {
		Sequence seq = new Sequence(Sequence.PPQ, PPQ);
		Track track = seq.createTrack();
		RhythmGenerator rgen = new RhythmGenerator();
		Iterable<Integer> rhythm = rgen.fillBars(4, 16);
		int ticks = 0;
		for (int noteLength : rhythm) {
			Note next = allNotes.next();
			int length = noteLength * (PPQ / 4); // PPQ / 4 is sixteenth note
			MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, 0, next.getValue(), 100);
			MidiEvent eventOn = new MidiEvent(msgOn, ticks);
			track.add(eventOn);
			MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, next.getValue(), 0);
			MidiEvent eventOff = new MidiEvent(msgOff, ticks + length);
			track.add(eventOff);
			ticks += length;
		}
		MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, 0, 0);
		MidiEvent eventOff = new MidiEvent(msgOff, ticks);
		track.add(eventOff);
		return seq;
	}

	private void play(MidiDevice toUse, Supplier<Sequence> seqSupplier) throws MidiUnavailableException, InvalidMidiDataException {
		Receiver rec = toUse.getReceiver();
		try {
			play(rec, seqSupplier);
		} finally {
			rec.close();
		}
	}

	private void play(Receiver rec, Supplier<Sequence> seqSupplier)
			throws MidiUnavailableException, InvalidMidiDataException {
		Sequencer seqr = MidiSystem.getSequencer(false);
		seqr.setTempoInBPM(120.0f);
		seqr.getTransmitter().setReceiver(rec);
		seqr.open();
		addMetaEventListener(seqr);
		try {
			while (true) {
				seqr.setSequence(seqSupplier.get());
				seqr.start();
				while (seqr.isRunning()) {
					sleep(200);
				}
			}
		} finally {
			seqr.close();
		}
	}

	private void addMetaEventListener(Sequencer seqr) {
		seqr.addMetaEventListener(new MetaEventListener() {
			@Override
			public void meta(MetaMessage meta) {
				if (END_OF_TRACK == meta.getType()) {
					MarkovSequenceRunner.this.notifyAll();
					/*
					 * if (seqr != null && seqr.isOpen()) {
					 * seqr.setTickPosition(0); seqr.start(); } else {
					 * MarkovSequenceRunner.this.notifyAll(); }
					 */
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

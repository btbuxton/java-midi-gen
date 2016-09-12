package net.blabux.midigen.research;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.IntStream;

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
	private static final Logger LOG = Logger.getLogger(MarkovSequenceRunner.class.getName());
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
		Chain chain = new ChainLoader(64).loadChain(url);
		MidiDevice device = getMidiDevice();
		device.open();
		try {
			Supplier<Sequence> seqSupplier = new Supplier<Sequence>() {
				Sequence current = null;
				@Override
				public Sequence get() {
					Iterator<Note> notes = new InfiniteIterator<>(chain);
					try {
						if (null == current) {
							current = createSequence(notes);
						} else {
							disintegrate(current);
							createTrack(notes, current);
						}
						return current;
					} catch (InvalidMidiDataException e) {
						throw new RuntimeException(e);
					}
				}
				
			};
			play(device, seqSupplier);
		} finally {
			device.close();
		}
	}

	private MidiDevice getMidiDevice() throws MidiUnavailableException {
		MidiDevice first = null;
		String toFind = System.getProperty("midiReceiver", "UM1");
		LOG.info("midiReceiver property set to: '" + toFind + "'");
		for (MidiDevice receiver : getReceivers()) {
			if (null == first) {
				first = receiver;
			}
			String name = receiver.getDeviceInfo().getName();
			LOG.info("Found Midi Receiver: " + name);
			if (name.contains(toFind)) {
				LOG.info("Matched: " + name);
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
		createTrack(allNotes, seq);
		return seq;
	}
	
	private Track createTrack(Iterator<Note> allNotes, Sequence seq) throws InvalidMidiDataException {
		Track track = seq.createTrack();
		RhythmGenerator rgen = new RhythmGenerator();
		Iterable<Integer> rhythm = rgen.fillBars(4, 16);
		int ticks = 0;
		Iterator<Integer> velocityGen = IntStream.iterate(100, i -> i <= 25 ? 100 : i - 25).iterator();
		for (int noteLength : rhythm) {
			Note next = allNotes.next();
			int length = noteLength * (PPQ / 4); // PPQ / 4 is sixteenth note
			MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, 0, next.getValue(), velocityGen.next());
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
		return track;
	}
	
	private void disintegrate(Sequence seq) {
		Track[] tracks = seq.getTracks();
		if (tracks.length > 4) {
			seq.deleteTrack(tracks[0]);
		}
		int index = 0;
		for (Track each : seq.getTracks()) {
			disintegrate(each, (index % 2) == 0);
			index++;
		}
	}

	private void disintegrate(Track track, boolean deleteFirst) {
		boolean deleteNext = deleteFirst;
		int noteOffToDelete = -1;
		List<MidiEvent> toDelete = new ArrayList<>();
		for (int index = 0; index < track.size(); index++) {
			MidiEvent event = track.get(index);
			int command = event.getMessage().getMessage()[0] & 0xFF;
			int note = event.getMessage().getMessage()[1] & 0xFF;
			if (0x90 == command) {
				if (deleteNext) {
					toDelete.add(event);
					deleteNext = false;
					noteOffToDelete = note;
				} else {
					deleteNext = true;
				}
			}
			if (0x80 == command && note == noteOffToDelete) {
				toDelete.add(event);
				noteOffToDelete = -1;
			}
		}
		for (MidiEvent event : toDelete) {
			track.remove(event);
		}
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
				seqr.setTickPosition(0);
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

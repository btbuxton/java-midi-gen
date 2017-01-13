package net.blabux.midigen.midi;

import java.util.function.Supplier;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class EndlessSequencer {
	private static final int WAIT_TIME_FOR_SEEQUENCE_END_MS = 200;
	private static final int END_OF_TRACK = 0x2F;

	public void start(float initialTempoBPM, Receiver rec, Supplier<Sequence> seqSupplier)
			throws MidiUnavailableException, InvalidMidiDataException {
		Sequencer seqr = MidiSystem.getSequencer(false);
		seqr.setSlaveSyncMode(Sequencer.SyncMode.MIDI_SYNC);
		seqr.setTempoInBPM(initialTempoBPM);
		seqr.getTransmitter().setReceiver(rec);
		seqr.open();
		Object lock = addMetaEventListener(seqr);
		try {
			Sequence seq = null;
			while (null != (seq = seqSupplier.get())) {
				seqr.setSequence(seq);
				seqr.start();
				while (seqr.isRunning()) {
					sleep(lock, WAIT_TIME_FOR_SEEQUENCE_END_MS);
				}
				seqr.setTickPosition(0);
			}
		} finally {
			seqr.close();
		}
	}

	private Object addMetaEventListener(Sequencer seqr) {
		final Object lock = new Object();
		seqr.addMetaEventListener((MetaMessage meta) -> {
			if (END_OF_TRACK == meta.getType()) {
				lock.notifyAll();
			}
		});
		return lock;
	}

	private void sleep(Object lock, long ms) {
		synchronized (this) {
			try {
				lock.wait(ms);
			} catch (InterruptedException e) {
				// IGNORE IT
			}
		}
	}
}

package net.blabux.midigen.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class SequenceRunner {
	private static final int END_OF_TRACK = 0x2F;

	private final Receiver receiver;
	private final Object lock;
	private float tempoBPM;
	private Sequencer sequencer;

	public SequenceRunner(Receiver receiver) {
		this(receiver, 120.0f);
	}
	
	public SequenceRunner(Receiver receiver, float tempoBPM) {
		this.receiver = receiver;
		this.lock = new Object();
		this.tempoBPM = tempoBPM;
	}
	
	public void open() throws MidiUnavailableException {
		sequencer = createSequencer();
	}

	public void close() {
		if (null != sequencer) {
			sequencer.close();
		}
		sequencer = null;
	}

	public void play(Sequence seq) throws InvalidMidiDataException {
		sequencer.setSequence(seq);
		sequencer.setTempoInBPM(tempoBPM);
		sequencer.setTickPosition(0);
		sequencer.start();
		while (sequencer.isRunning()) {
			sleep(200);
		}
	}

	public void loop(Iterable<Sequence> sequences) throws MidiUnavailableException, InvalidMidiDataException {
		open();
		try {
			for (Sequence seq : sequences) {
				play(seq);
			}
		} finally {
			close();
		}
	}
	
	public void setTempoBPM(float newTempo) {
		this.tempoBPM = newTempo;
	}
	
	public float getTempoBPM() {
		return tempoBPM;
	}

	private Sequencer createSequencer() throws MidiUnavailableException {
		Sequencer seqr = MidiSystem.getSequencer(false);
		seqr.setSlaveSyncMode(Sequencer.SyncMode.MIDI_SYNC);
		seqr.getTransmitter().setReceiver(receiver);
		seqr.open();
		addMetaEventListener(seqr);
		return seqr;
	}

	private void sleep(long ms) {
		synchronized (lock) {
			try {
				lock.wait(ms);
			} catch (InterruptedException e) {
				// IGNORE IT
			}
		}
	}

	private void addMetaEventListener(Sequencer seqr) {
		seqr.addMetaEventListener(new MetaEventListener() {
			@Override
			public void meta(MetaMessage meta) {
				if (END_OF_TRACK == meta.getType()) {
					SequenceRunner.this.lock.notifyAll();
				}
			}
		});

	}
}

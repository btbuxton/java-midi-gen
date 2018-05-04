package net.blabux.midigen.midi.fixed;

import javax.sound.midi.*;

public class SequenceRunner implements AutoCloseable {
    private static final int END_OF_TRACK = 0x2F;

    private final Receiver receiver;
    private final Object lock;
    private float tempoBPM;
    private Sequencer sequencer;

    public SequenceRunner(Receiver receiver) throws MidiUnavailableException {
        this(receiver, 120.0f);
    }

    public SequenceRunner(Receiver receiver, float tempoBPM) throws MidiUnavailableException {
        this.receiver = receiver;
        this.lock = new Object();
        this.tempoBPM = tempoBPM;
        open();
    }

    private void open() throws MidiUnavailableException {
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
        for (Sequence seq : sequences) {
            play(seq);
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

package net.blabux.midigen.composition;

import net.blabux.midigen.common.Note;
import net.blabux.midigen.common.RingIterator;
import net.blabux.midigen.markov.Chain;
import net.blabux.midigen.markov.ChainLoader;
import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.random.RhythmGenerator;

import javax.sound.midi.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * This is the code used to create my composition "Alien Finger Bracelet".
 * Assumes you have an external midi controller running on midi channels 1-4.
 * <p>
 * I train a markov chain N deep with a midi file from a composition that I love.
 * I then generate 4 separate melodic lines and then degenerate them.
 * <p>
 * This is my first generative music program. Enjoy!
 *
 * @author btbuxton
 */
public class  AlienFingerBracelet {
    private static final Logger LOG = Logger.getLogger(AlienFingerBracelet.class.getName());
    private static final int PPQ = 24;
    private static final int END_OF_TRACK = 0x2F;

    public static void main(String[] args) {
        AlienFingerBracelet runner = new AlienFingerBracelet();
        try {
            runner.run();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
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
                    Iterator<Note> notes = new RingIterator<>(chain);
                    try {
                        if (null == current) {
                            current = createSequence(notes);
                        } else {
                            createTrack(notes, current);
                            disintegrate(current);
                        }
                        return current;
                    } catch (InvalidMidiDataException e) {
                        throw new RuntimeException(e);
                    }
                }

            };
            final Receiver rec = device.getReceiver();
            try {
                play(rec, seqSupplier);
            } finally {
                sleep(1000);
                rec.close();
            }
        } finally {
            device.close();
        }
    }

    private MidiDevice getMidiDevice() {
        String toFind = System.getProperty("midiReceiver", "UM1");
        LOG.info("midiReceiver property set to: '" + toFind + "'");
        for (String name : MidiUtil.getMidiReceiverNames()) {
            LOG.info("Possible midi device: " + name);
        }
        MidiDevice result = MidiUtil.getMidiReceiversContainingNameOrDefault(toFind);
        LOG.info("Using midi device: " + result.getDeviceInfo().getName());
        return result;
    }

    private Sequence createSequence(Iterator<Note> allNotes) throws InvalidMidiDataException {
        Sequence seq = new Sequence(Sequence.PPQ, PPQ);
        createTrack(allNotes, seq);
        return seq;
    }

    // i -> random.nextInt(100) + 25
    Iterator<Integer> velocityGen = IntStream.iterate(99, i -> i <= 33 ? 99 : i - 33).iterator();
    Iterator<Integer> channelGen = new RingIterator<>(Arrays.asList(0, 1, 2, 3));
    Iterator<Integer> tempo = new RingIterator<>(Arrays.asList(16, 32, 8));

    private Track createTrack(Iterator<Note> allNotes, Sequence seq) throws InvalidMidiDataException {
        Track track = seq.createTrack();
        RhythmGenerator rgen = new RhythmGenerator();
        Iterable<Integer> rhythm = rgen.fillBars(4, tempo.next());
        int ticks = 0;
        int channel = channelGen.next();
        LOG.info("Created track with channel: " + channel);
        for (int noteLength : rhythm) {
            Note next = allNotes.next();
            int length = noteLength * (PPQ / 4); // PPQ / 4 is sixteenth note
            MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, channel, next.getValue(), velocityGen.next());
            MidiEvent eventOn = new MidiEvent(msgOn, ticks);
            track.add(eventOn);
            MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, next.getValue(), 0);
            MidiEvent eventOff = new MidiEvent(msgOff, ticks + (int) (length * 0.9));
            track.add(eventOff);
            ticks += length;
        }
        MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, 0, 0);
        MidiEvent eventOff = new MidiEvent(msgOff, ticks - 1);
        track.add(eventOff);
        return track;
    }

    private void disintegrate(Sequence seq) {
        Track[] tracks = seq.getTracks();
        if (tracks.length > 4) {
            LOG.info("Removing: " + tracks[0] + "  because tracks is size " + tracks.length);
            seq.deleteTrack(tracks[0]);
        }
        int index = 0;
        for (Track each : seq.getTracks()) {
            if (index > 2) {
                disintegrate(each, (index % 2) == 0);
            }
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

    private void play(Receiver rec, Supplier<Sequence> seqSupplier)
            throws MidiUnavailableException, InvalidMidiDataException {
        Sequencer seqr = MidiSystem.getSequencer(false);
        seqr.setSlaveSyncMode(Sequencer.SyncMode.MIDI_SYNC);
        seqr.setTempoInBPM(120.0f);
        seqr.getTransmitter().setReceiver(rec);
        seqr.open();
        addMetaEventListener(seqr);
        try {
            for (int index = 0; index < 12; index++) {
                LOG.info("Iteration: " + index);
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
                    AlienFingerBracelet.this.notifyAll();
                    /*
                     * if (seqr != null && seqr.isOpen()) {
                     * seqr.setTickPosition(0); seqr.start(); } else {
                     * AlienFingerBracelet.this.notifyAll(); }
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

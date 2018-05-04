package net.blabux.midigen.composition;

import net.blabux.midigen.common.Note;
import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.midi.fixed.SequenceRunner;
import net.blabux.midigen.research.loader.CurrentState;
import net.blabux.midigen.research.loader.SequenceStateIterator;

import javax.sound.midi.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Research for MidiFileStateIterable and CurrentState....
 *
 * @author btbuxton
 */
public class InvisibleEmpire {
    final Random random = new Random(0); //this is so it plays the same way everytime

    void run(Sequence input, double bpm) throws Exception {
        Sequence seq = createOutput(input);
        MidiDevice device = getMidiDevice();
        device.open();
        try {
            try (Receiver recv = device.getReceiver()) {
                try (SequenceRunner runner = new SequenceRunner(recv, (float) bpm)) {
                    runner.play(seq);
                }
            }
        } finally {
            device.close();
        }
    }

    Supplier<Optional<CurrentState>> stateSupplier(Sequence input, double beatsPerStep) {
        final SequenceStateIterator events = SequenceStateIterator.create(input);
        final CurrentState state = events.getState();
        events.fastForward((event) -> state.hasAnyNotes());
        AtomicLong tick = new AtomicLong(state.getTick());
        final long step = (long) (state.getPPQ() * beatsPerStep);
        return () -> {
            if (events.hasNext()) {
                events.fastForward(tick.getAndAdd(step));
                return Optional.of(state);
            } else {
                return Optional.empty();
            }
        };
    }

    Sequence createOutput(Sequence input) throws InvalidMidiDataException {
        final Sequence seq = new Sequence(Sequence.PPQ, 240);
        final long length = seq.getResolution() * 4 * 50; // (50 bars of 4 beats)
        createTrack(seq, input, 0, length, 2.0);
        createTrack(seq, input, 1, length, 1.75);
        createTrack(seq, input, 2, length, 1.5);
        createTrack(seq, input, 3, length, 1.25);
        return seq;
    }

    void createTrack(Sequence output, Sequence input, int channel, long length, double beatsPerStep) throws InvalidMidiDataException {
        final Track track = output.createTrack();
        final Supplier<Optional<CurrentState>> stateSupplier = stateSupplier(input, beatsPerStep);
        final Function<CurrentState, Optional<Map.Entry<Note, Integer>>> noteSelector = noteSelector();
        long current = 0;
        long step = (long) (output.getResolution() * beatsPerStep);
        long gate = step / 2; // 50% gate
        Optional<CurrentState> state = Optional.empty();
        do {
            state = stateSupplier.get();
            if (state.isPresent()) {
                Optional<Map.Entry<Note, Integer>> selected = noteSelector.apply(state.get());
                if (selected.isPresent()) {
                    MidiMessage on = new ShortMessage(ShortMessage.NOTE_ON, channel, selected.get().getKey().getValue(), selected.get().getValue());
                    track.add(new MidiEvent(on, current));
                    MidiMessage off = new ShortMessage(ShortMessage.NOTE_OFF, channel, selected.get().getValue(), 0);
                    track.add(new MidiEvent(off, current + gate));
                }
            }
            current += step;
        } while (current < length && state.isPresent());
        //fake note off to mark end of track so it plays last note without cutting off
        MidiMessage off = new ShortMessage(ShortMessage.NOTE_OFF, channel, 0, 0);
        track.add(new MidiEvent(off, current));
    }

    Function<CurrentState, Optional<Map.Entry<Note, Integer>>> noteSelector() {
        return (state) -> {
            List<Map.Entry<Note, Integer>> notes = state.getAllNotesAndVelocities();
            if (notes.isEmpty()) {
                return Optional.empty();
            }
            int index = random.nextInt(notes.size());
            return Optional.of(notes.get(index));
        };
    }

    MidiDevice getMidiDevice() {
        String toFind = System.getProperty("recv", "");
        System.out.println("midiReceiver property set to: '" + toFind + "'");
        for (String name : MidiUtil.getMidiReceiverNames()) {
            System.out.println("Possible midi device: " + name);
        }
        MidiDevice result = MidiUtil.getMidiReceiversContainingNameOrDefault(toFind);
        System.out.println("Using midi device: " + result.getDeviceInfo().getName());
        return result;
    }

    public static void main(String[] args) throws Exception {
        InvisibleEmpire inst = new InvisibleEmpire();
        //URL url = OutOfSync.class.getResource("/RunningLate-DanWheeler.mid");
        URL url = new File("/home/btbuxton/Music/midi/concerto_11_1_(c)oguri.mid").toURI().toURL();
        Sequence input = MidiSystem.getSequence(url);
        //play it straight
		/*
		MidiDevice device = inst.getMidiDevice();
		device.open();
		Receiver recv = device.getReceiver();
		try (SequenceRunner runner = new SequenceRunner(recv, 108)) {
			runner.play(input);
		}
		*/
        inst.run(input, 60);
    }
}

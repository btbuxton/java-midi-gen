package net.blabux.midigen.research.loader;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import net.blabux.midigen.common.Note;
import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.midi.fixed.SequenceRunner;
import net.blabux.midigen.research.loader.MidiFileStateIterable.StateIterator;

/**
 * Research for MidiFileStateIterable and CurrentState....
 * @author btbuxton
 *
 */
public class OutOfSync {
	final Random random = new Random(System.currentTimeMillis());

	void run(URL url, double bpm) throws Exception {
		Sequence seq = createSequence(url);
		MidiDevice device = getMidiDevice();
		device.open();
		try {
			try (Receiver recv = device.getReceiver()) {
				try (SequenceRunner runner = new SequenceRunner(recv, (float)bpm)) {
					runner.play(seq);
				}
			}
		} finally {
			device.close();
		}
	}

	Supplier<Optional<CurrentState>> stateSupplier(URL input, double beatsPerStep) throws Exception {
		final MidiFileStateIterable events = new MidiFileStateIterable(input);
		final StateIterator stateIterator = events.stateIterator();
		final CurrentState state = stateIterator.getState();
		stateIterator.fastForward((event) -> state.hasAnyNotes());
		AtomicLong tick = new AtomicLong(state.getTick());
		final long step = (long) (state.getPPQ() * beatsPerStep);
		return () -> {
			if (stateIterator.hasNext()) {
				stateIterator.fastForward(tick.getAndAdd(step));
				return Optional.of(state);
			} else {
				return Optional.empty();
			}
		};
	}

	Sequence createSequence(URL input) throws Exception {
		final Sequence seq = new Sequence(Sequence.PPQ, 240);
		final long length = seq.getResolution() * 4 * 25; // (25 bars of 4 beats)
		createTrack(seq, input, 0, length, 2.0);
		createTrack(seq, input, 1, length, 1.0);
		createTrack(seq, input, 2, length, 0.5);
		createTrack(seq, input, 3, length, 0.25);
		return seq;
	}
	
	void createTrack(Sequence seq, URL input,  int channel, long length, double beatsPerStep) throws Exception {
		final Track track = seq.createTrack();
		final Supplier<Optional<CurrentState>> stateSupplier = stateSupplier(input, beatsPerStep);
		long current = 0;
		long step = (long)(seq.getResolution() * beatsPerStep);
		long gate = step / 2; // 50% gate
		Optional<CurrentState> state = Optional.empty();
		do {
			state = stateSupplier.get();
			if (state.isPresent()) {
				List<Note> notes = state.get().getAllNotes();
				if (notes.size() > 0) {
					int index = random.nextInt(notes.size());
					Note selected = notes.get(index);
					MidiMessage on = new ShortMessage(ShortMessage.NOTE_ON, channel, selected.getValue(), 100);
					track.add(new MidiEvent(on, current));
					MidiMessage off = new ShortMessage(ShortMessage.NOTE_OFF, channel, selected.getValue(), 0);
					track.add(new MidiEvent(off, current + gate));
				}
			}
			current += step;
		} while (current < length && state.isPresent());
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
		OutOfSync inst = new OutOfSync();
		URL url = OutOfSync.class.getResource("/RunningLate-DanWheeler.mid");
		//URL url = new File("/home/btbuxton/Music/midi/concerto_11_1_(c)oguri.mid").toURI().toURL();
		inst.run(url, 90);
	}
}

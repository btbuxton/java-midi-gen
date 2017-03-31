package net.blabux.midigen.example;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import net.blabux.midigen.common.InfiniteIterable;
import net.blabux.midigen.common.Note;
import net.blabux.midigen.common.RingIterator;
import net.blabux.midigen.common.Scale;
import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.midi.SequenceRunner;
import net.blabux.midigen.research.LFOIterator;
import net.blabux.midigen.research.TrackBuilder;

public class ExampleSequence {
	private static final Logger LOG = Logger.getLogger(ExampleSequence.class.getName());
	private static final int PPQ = 24;

	public static void main(String[] args) {
		try {
			new ExampleSequence().run();
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}

	}

	private MidiDevice getMidiDevice() {
		String toFind = System.getProperty("midiReceiver", "UM1");
		LOG.info("midiReceiver property set to: '" + toFind + "'");
//		for (String name : MidiUtil.getMidiReceiverNames()) {
//			LOG.info("Possible midi device: " + name);
//		}
		MidiDevice result = MidiUtil.getMidiReceiversContainingNameOrDefault(toFind);
		LOG.info("Using midi device: " + result.getDeviceInfo().getName());
		return result;
	}

	private void run() throws MidiUnavailableException, InvalidMidiDataException {
		MidiDevice device = getMidiDevice();
		device.open();
		try {
			run(device);
		} finally {
			device.close();
		}
	}

	private void run(MidiDevice device) throws MidiUnavailableException, InvalidMidiDataException {
		Receiver rec = device.getReceiver();
		SequenceRunner runner = new SequenceRunner(rec, 96f);
		try {
			runner.loop(new InfiniteIterable<Sequence>(() -> {
				try {
					LOG.info(String.format("Tempo: %s", String.valueOf(runner.getTempoBPM())));
					Sequence seq = new Sequence(Sequence.PPQ, PPQ);
					createTrack(seq);
					return seq;
				} catch(InvalidMidiDataException ex) {
					throw new IllegalStateException(ex);
				}
			}));
		} finally {
			rec.close();
		}
	}

	private Track createTrack(Sequence seq) throws InvalidMidiDataException {
		TrackBuilder track = new TrackBuilder(seq,0);
		int note16 = seq.getResolution() / 4;
		long ticks = 0;
		int[] rhythm = new int[] { 1, 1, 4, 2, 8, 8, 2, 4, 1, 1 };
		List<Note> scale = Scale.MINOR_PENT.notes(Note.named("E2"));
		Collections.shuffle(scale);
		Iterator<Note> notes = new RingIterator<>(scale);

		for (int noteLength : rhythm) {
			Note next = notes.next();
			int length = note16 * noteLength;
			track.note(ticks, next, (long)(length * 0.5));
			ticks += length;
		}
		track.placebo(ticks);
		
		Iterator<Integer> lfo = new LFOIterator(seq.getResolution(), 0.25, 64, 64);
		Iterator<Integer> lfo2 = new LFOIterator(seq.getResolution(), 0.75, 96, 32);
		Iterator<Integer> lfo3 = new LFOIterator(seq.getResolution(), 0.3333 / 2, 64, 32);
		for (int pulse = 0; pulse < ticks; pulse++) {
			track.cc(pulse, 74, lfo.next());
			track.cc(pulse, 7, lfo2.next());
			track.cc(pulse, 10, lfo3.next());
		}
		return track.build();
	}

}

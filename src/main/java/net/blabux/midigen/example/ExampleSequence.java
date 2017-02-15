package net.blabux.midigen.example;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import net.blabux.midigen.common.RingIterator;
import net.blabux.midigen.common.InfiniteIterable;
import net.blabux.midigen.common.Note;
import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.midi.SequenceRunner;

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
		SequenceRunner runner = new SequenceRunner(rec, 86f);
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
		Track track = seq.createTrack();
		int note16 = seq.getResolution() / 4;
		long ticks = 0;
		int[] rhythm = new int[] { 1, 1, 4, 2, 8, 8, 2, 4, 1, 1 };
		Iterator<Note> notes = new RingIterator<>(Note.BY_NAME.get("E2").scale(Note.MINOR_PENT));

		for (int noteLength : rhythm) {
			Note next = notes.next();
			int length = note16 * noteLength;
			MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, 0, next.getValue(), 100);
			MidiEvent eventOn = new MidiEvent(msgOn, ticks);
			track.add(eventOn);
			MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, next.getValue(), 0);
			MidiEvent eventOff = new MidiEvent(msgOff, ticks + (int) (length * 0.5));
			track.add(eventOff);
			ticks += length;
		}
		MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, 0, 0);
		MidiEvent eventOff = new MidiEvent(msgOff, ticks);
		track.add(eventOff);
		//LOG.info(String.format("ticks %d", track.ticks()));
		return track;
	}

}

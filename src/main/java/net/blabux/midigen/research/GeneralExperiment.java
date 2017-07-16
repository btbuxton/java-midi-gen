package net.blabux.midigen.research;

import java.util.List;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import net.blabux.midigen.common.Note;
import net.blabux.midigen.common.Scale;
import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.midi.fixed.SequenceRunner;

/**
 *
 */
//https://dsp.stackexchange.com/questions/1645/converting-a-pitch-bend-midi-value-to-a-normal-pitch-value
public class GeneralExperiment {
	final Random random = new Random(0); //this is so it plays the same way everytime

	void run(double bpm) throws Exception {
		Sequence seq = createOutput();
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

	Sequence createOutput() throws InvalidMidiDataException {
		final Sequence seq = new Sequence(Sequence.PPQ, 240);
		createTrack(seq);
		return seq;
	}

	void createTrack(Sequence seq) throws InvalidMidiDataException {
		Track track = seq.createTrack();
		int channel = 0;
		long tick = 0;
		ShortMessage msg;
		msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0, 1); //bank works
		track.add(new MidiEvent(msg, tick++));
		msg = new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, 6); //WTF
		track.add(new MidiEvent(msg, tick++));
		msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 65, 127); //portamento on
		track.add(new MidiEvent(msg, tick++));
		msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 5, 64); //portamento
		track.add(new MidiEvent(msg, tick++));
		msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 84, 127); //portamento
		track.add(new MidiEvent(msg, tick++));
		//msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 10, 0); //volume not
		//track.add(new MidiEvent(msg, tick++));
		//msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 1, 127); //mod works
		//track.add(new MidiEvent(msg, tick++));
		//msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 10, 64); //pan somehwat
		//track.add(new MidiEvent(msg, tick++));
		//msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 8, 0); //balance not
		//track.add(new MidiEvent(msg, tick++));

		
		long ppq = seq.getResolution();
		tick += 16;
		double div = 0.5;
		double gate = 1.0;
		List<Note> notes = Scale.MINOR_PENT.notes(Note.named("E3"));
		for (int i=0; i < 256; i++) {
			Note note = notes.get(random.nextInt(notes.size()));
			ShortMessage onMsg = new ShortMessage(ShortMessage.NOTE_ON, channel, note.getValue(), 100);
			track.add(new MidiEvent(onMsg, tick));
			ShortMessage offMsg = new ShortMessage(ShortMessage.NOTE_OFF, channel, note.getValue(), 0);
			track.add(new MidiEvent(offMsg, (long) (tick + (gate * div * ppq))));
			//for (int sub=0; sub < (div *ppq) / 2; sub += 1) {
			//	ShortMessage pb = new ShortMessage(ShortMessage.PITCH_BEND, channel, 0, Math.abs(64 - sub) % 128);
			//	track.add(new MidiEvent(pb, tick + sub * 2));
			//}
			tick += div * ppq;
		}
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
		GeneralExperiment inst = new GeneralExperiment();
		inst.run(60);
	}
}

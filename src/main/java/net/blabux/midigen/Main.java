package net.blabux.midigen;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;

public class Main {

	public static void main(String[] args) {
		Main main = new Main();
		try {
			main.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void run() throws MidiUnavailableException, InvalidMidiDataException {
		MidiDevice toUse = null;
		for (MidiDevice receiver : getReceivers()) {
			if (receiver.getDeviceInfo().getName().startsWith("Boutiq")) {
				toUse = receiver;
				break;
			}
		}
		if (null == toUse)
			return;
		playSingleNote(toUse);
		sleep(1000);
		playSimpleSequence(toUse);
	}

	private void playSimpleSequence(MidiDevice toUse) throws MidiUnavailableException, InvalidMidiDataException {
		Sequence seq = new Sequence(Sequence.PPQ, 24);
		Track track = seq.createTrack();
		short[] notes = { 60, 67, 72, 67 };
		int ticks = 0;
		for (short each : notes) {
			MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, 0, each, 100);
			MidiEvent eventOn = new MidiEvent(msgOn, ticks);
			track.add(eventOn);
			MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, each, 0);
			MidiEvent eventOff = new MidiEvent(msgOff, ticks + 20);
			track.add(eventOff);
			ticks += 24;
		}
		MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, 0, 0, 0);
		MidiEvent eventOff = new MidiEvent(msgOff, ticks);
		track.add(eventOff);
		System.out.println("seq tick length: " + seq.getTickLength());
		toUse.open();
		try {
			Receiver rec = toUse.getReceiver();
			try {
				Sequencer seqr = MidiSystem.getSequencer(false);
				seqr.setSequence(seq);
				seqr.setTempoInBPM(120.0f);
				seqr.getTransmitter().setReceiver(rec);
				seqr.open();
				seqr.setLoopCount(3); // Sequencer.LOOP_CONTINUOUSLY
				seqr.setLoopEndPoint(-1);
				addMetaEventListener(seqr);
				System.out.println("loop start: " + seqr.getLoopStartPoint());
				System.out.println("loop end: " + seqr.getLoopEndPoint());
				System.out.println("sequencer ticks: " + seqr.getTickLength());
				try {
					seqr.start();
					while (seqr.isRunning()) {
						sleep(200);
					}
				} finally {
					seqr.close();
				}

			} finally {
				rec.close();
			}
		} finally {
			toUse.close();
		}

	}

	private void addMetaEventListener(Sequencer seqr) {
		int endOftrackMessage = 47;
		seqr.addMetaEventListener(new MetaEventListener() {

			@Override
			public void meta(MetaMessage meta) {
				if (endOftrackMessage == meta.getType()) {
					System.out.println("DONE!");
				}
			}
		});

	}

	private void playSingleNote(MidiDevice toUse) throws MidiUnavailableException, InvalidMidiDataException {
		toUse.open();
		try {
			Receiver receiver = toUse.getReceiver();
			try {
				ShortMessage myMsg = new ShortMessage();
				// Start playing the note Middle C (60),
				// moderately loud (velocity = 93).
				myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
				long timeStamp = -1;
				receiver.send(myMsg, timeStamp);
				sleep(1000);
				myMsg.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0);
				receiver.send(myMsg, -1);
			} finally {
				receiver.close();
			}
		} finally {
			toUse.close();
		}
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

}

package net.blabux.midigen.example;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.midi.realtime.Pulse;

public class Experiment {
	private static final int NOW = -1;

	public static void main(String[] args) {
		try {
			MidiDevice device = MidiUtil.getMidiReceiversContainingNameOrDefault("electribe2");
			Receiver recvr = device.getReceiver();
			device.open();
			try {
				final MidiMessage msg = new ShortMessage(ShortMessage.TIMING_CLOCK);
				new Pulse(240.0).run((tick) -> {
					recvr.send(msg, NOW);
					return tick < (24 * 120);
				}); 
				/*
				Note d2 = Note.BY_NAME.get("D2");
				MidiMessage msg = new ShortMessage(ShortMessage.NOTE_ON, 0, d2.getValue(), 96);
				recvr.send(msg, NOW);
				Thread.sleep(500);
				msg = new ShortMessage(ShortMessage.NOTE_OFF, 0, d2.getValue(), 96);
				*/
			} finally {
				recvr.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}

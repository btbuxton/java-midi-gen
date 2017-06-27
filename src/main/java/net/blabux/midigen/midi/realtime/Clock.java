package net.blabux.midigen.midi.realtime;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class Clock implements PulseFunc {
	private static final ShortMessage MIDI_CLOCK_MSG;
	private static final int PPQ = 24;
	private final Receiver midiRecv;
	private final long pulsesToWait;

	static {
		try {
			MIDI_CLOCK_MSG = new ShortMessage(ShortMessage.TIMING_CLOCK);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	public Clock(Receiver recv, PulseGen gen) {
		this.midiRecv = recv;
		this.pulsesToWait = calculatePulsesToWaitToSendClock(gen);
	}

	@Override
	public boolean pulse(long tick) {
		if (tick % pulsesToWait == 0) {
			sendClock();
		}
		return true;
	}

	private void sendClock() {
		midiRecv.send(MIDI_CLOCK_MSG, -1);
	}
	
	private long calculatePulsesToWaitToSendClock(PulseGen gen) {
		long tpb = gen.ticks(1);
		if (tpb < PPQ) {
			throw new IllegalStateException("Too few pulses: " + tpb + ", need " + PPQ);
		}
		if (tpb % PPQ != 0) {
			throw new IllegalStateException("Pulses: " + tpb + " must be divisible by " + PPQ);
		}
		return tpb / PPQ;
	}

}

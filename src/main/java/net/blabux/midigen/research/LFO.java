package net.blabux.midigen.research;

import javax.sound.midi.InvalidMidiDataException;

public class LFO {
	final long ppq; // pulses per quarter note
	final double cpq; // cycles per quarter note
	final int center; // center of LFO
	final int depth;

	public LFO(long ppq, double cpq, int center, int depth) {
		this.ppq = ppq;
		this.cpq = cpq;
		this.center = center;
		this.depth = depth;
	}

	public void generate(TrackBuilder track, long startInTicks, long length, int cc) throws InvalidMidiDataException {
		int value = 0;
		double phase = Math.toRadians(0); // might nice to specify as well
		double degStep = (360.0 / ppq) * cpq;
		double radStep = Math.toRadians(degStep);
		for (int offset = 0; offset < length; offset++) {
			value = (int) (center + (depth * Math.sin(phase)));
			track.cc(startInTicks + offset, cc, Math.min(127, Math.max(0, value)));
			phase += radStep;
		}
	}
}

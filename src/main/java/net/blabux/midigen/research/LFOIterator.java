package net.blabux.midigen.research;

import java.util.Iterator;

public class LFOIterator implements Iterator<Integer> {
	final long ppq; // pulses per quarter note
	final double cpq; // cycles per quarter note
	final int center; // center of LFO
	final int depth;
	final double radStep;
	double phase;
	
	

	public LFOIterator(long ppq, double cpq, int center, int depth) {
		this.ppq = ppq;
		this.cpq = cpq;
		this.center = center;
		this.depth = depth;
		phase = Math.toRadians(0);
		double degStep = (360.0 / ppq) * cpq;
		radStep = Math.toRadians(degStep);
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Integer next() {
		int value = (int) (center + (depth * Math.sin(phase)));
		phase += radStep;
		return Math.min(127, Math.max(0, value));
	}
}

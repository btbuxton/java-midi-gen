package net.blabux.midigen.research;

import java.util.Iterator;

public class LFOIterator implements Iterator<Integer> {
	final int center; // center of LFO
	final int depth;
	final double radStep;
	double phase;
	
	

	public LFOIterator(long ppq, double cpq, int center, int depth) {
		this.center = center;
		this.depth = depth;
		double degStep = 360.0 * cpq / ppq;
		radStep = Math.toRadians(degStep);
		phase = Math.toRadians(0);
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Integer next() {
		int value = (int)Math.round(center + (depth * Math.sin(phase)));
		phase += radStep;
		return Math.min(127, Math.max(0, value));
	}
}

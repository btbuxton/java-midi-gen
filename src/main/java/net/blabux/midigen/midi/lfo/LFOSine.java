package net.blabux.midigen.midi.lfo;

public class LFOSine extends LFOAbstract {
	final int center; // center of LFO
	final int depth;
	final double radStep;
	double phase;
	
	public LFOSine(long ppq, double cpq, int center, int depth) {
		this.center = center;
		this.depth = depth;
		double degStep = 360.0 * cpq / ppq;
		radStep = Math.toRadians(degStep);
		phase = Math.toRadians(0);
	}

	@Override
	public Integer next() {
		int value = (int)Math.round(center + (depth * Math.sin(phase)));
		phase += radStep;
		return scrub(value);
	}
}

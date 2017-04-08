package net.blabux.midigen.midi.lfo;

public abstract class LFOAbstract implements LFO {
	final int center;
	final int depth;

	//This does nothing, but's to keep everything consistent
	public LFOAbstract(long ppq, double cpq, int center, int depth) {
		this.center = center;
		this.depth = depth;
	}
	
	@Override
	public boolean hasNext() {
		return true;
	}
	
	/**
	 * Ensure the input integer is between 0-127
	 * @param input
	 * @return
	 */
	int scrub(int input) {
		return Math.min(127, Math.max(0, input));
	}
	
	int scrub(double input) {
		return scrub((int)Math.round(input));
	}
}

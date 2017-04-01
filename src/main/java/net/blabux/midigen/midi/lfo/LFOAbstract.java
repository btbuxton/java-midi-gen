package net.blabux.midigen.midi.lfo;

public abstract class LFOAbstract implements LFO {
	
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
}

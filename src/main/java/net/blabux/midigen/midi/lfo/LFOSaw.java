package net.blabux.midigen.midi.lfo;

public class LFOSaw extends LFOAbstract {
	final double slope;
	final long length;
	final long start;
	final int dir;
	long index;

	/**
	 * Starts in the middle (or close to) /| / / | / --|--/----center | / |/
	 * 
	 * @param ppq
	 * @param cpq
	 * @param center
	 * @param depth
	 */
	public LFOSaw(long ppq, double cpq, int center, int depth, boolean rise) {
		super(ppq, cpq, center, depth);
		start = center - depth;
		length = Math.round(ppq / cpq);
		slope = (double) (2 * depth) / (length - 1);
		index = rise ? 0 : length - 1;
		dir = rise ? 1 : -1;
	}

	@Override
	public Integer next() {
		double current = start + (slope * index);
		index += dir;
		if (index >= length) {
			index = 0;
		} else if (index < 0) {
			index = length - 1;
		}
		return scrub(current);
	}

}

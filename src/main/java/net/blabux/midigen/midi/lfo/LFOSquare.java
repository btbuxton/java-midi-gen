package net.blabux.midigen.midi.lfo;

public class LFOSquare extends LFOAbstract {
	long index;
	long length;
	int dir;
	
	//TODO
	public LFOSquare(long ppq, double cpq, int center, int depth, boolean startLow) {
		super(ppq, cpq, center, depth);
		int low = center - depth;
		int high = center + depth;
		dir = startLow ? -1 : 1;
		
	}

	@Override
	public Integer next() {
		return 0;
	}

}

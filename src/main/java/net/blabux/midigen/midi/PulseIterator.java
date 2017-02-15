package net.blabux.midigen.midi;

import java.util.Iterator;

/**
 * Experimental class that uses a pull model instead of push
 * The timing accuracy is better (2 minutes run is within 1ms)
 * 
 * @author btbuxton
 *
 */
public class PulseIterator implements Iterator<Long> {
	private static final long MS = 1000000; // nanoseconds per ms
	private static final double DEFAULT_TEMPO_BPM = 120.0;
	private static final int PPQ = 24;

	private long start;
	private boolean shouldStop;
	private long tick;
	private double mspp;
	private double tempoBPM;

	public PulseIterator() {
		this(DEFAULT_TEMPO_BPM);
	}

	public PulseIterator(double tempoBPM) {
		setTempoBPM(tempoBPM);
		tick = 0;
		start = 0;
		shouldStop = false;
	}

	@Override
	public boolean hasNext() {
		return !shouldStop;
	}

	@Override
	public Long next() {
		if (start > 0) {
			final long now = System.nanoTime();
			//need to change this to a moving start....
			long diff = (long) ((tick * mspp) - (now - start) / MS);
			if (diff > 0) {
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					// IGNORE
				}
			}
		} else {
			start = System.nanoTime();
		}
		return tick++;
	}

	public void setTempoBPM(double bpm) {
		tempoBPM = bpm;
		mspp = 60.0 / PPQ / tempoBPM * 1000;
	}

	public void stop() {
		shouldStop = true;
	}

	/*
	 * This is for testing only
	 */

	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		PulseIterator iter = new PulseIterator();
		while (iter.next() < (PPQ * 240)) {
			// do nothing
		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

}

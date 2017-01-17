package net.blabux.midigen.midi;

import java.util.function.Function;

/**
 * Creates a pulse of 24 per quarter note/beat at supplied BPM
 * @author btbuxton
 *
 */
public class Pulse {
	private static final long MS = 1000000; // nanoseconds per ms
	private static final double DEFAULT_TEMPO_BPM = 120.0;
	private static final int PPQ = 24;
	
	private double tempoBPM;
	private volatile double mspp;
	
	public Pulse() {
		this(DEFAULT_TEMPO_BPM);
	}
	
	public Pulse(double bpm) {
		setTempoBPM(bpm);
	}
	
	public void pulse(final Function<Long, Boolean> pulseFunc) {
		long tick = 0;
		final long start = System.nanoTime();
		long tickEnd;
		while (pulseFunc.apply(tick++)) {
			tickEnd = System.nanoTime();
			long diff = (long)((tick * mspp) - (tickEnd - start) / MS);
			try {
				//if (diff < 0) {
					//System.out.println("falling behind by: "+diff);
				//}
				if (diff > 0) {
					Thread.sleep(diff);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public void setTempoBPM(double bpm) {
		tempoBPM = bpm;
		mspp = 60.0 / PPQ / tempoBPM * 1000;
	}
	
	
	/*
	 * This is for testing only
	 */
	
	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		new Pulse().pulse((tick) -> {
			return tick < (PPQ * 240);
		}); 
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}
	
}

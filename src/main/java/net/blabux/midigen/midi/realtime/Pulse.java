package net.blabux.midigen.midi.realtime;

import java.util.function.Function;

/**
 * Creates a pulse of 24 per quarter note/beat at supplied BPM
 * Has the ability to change tempo while running see main()
 * 
 * To do:
 * 1. Time function and warn if it's longer than a wait cycle
 * 2. Allow other ppqs higher than 24
 * 
 * @author btbuxton
 *
 */
public class Pulse {
	private static final long INITIAL_START_VALUE = -1;
	private static final double DEFAULT_TEMPO_BPM = 120.0;
	private static final int PPQ = 24;

	private volatile double tempoBPM;
	private volatile double mspp;
	private volatile long start;

	public Pulse() {
		this(DEFAULT_TEMPO_BPM);
	}

	public Pulse(double bpm) {
		setTempoBPM(bpm);
	}

	public void pulse(final Function<Long, Boolean> pulseFunc) {
		long tick = 0;
		long timeTick = 0;
		long tickEnd;
		while(pulseFunc.apply(tick++)) {
			timeTick = start == INITIAL_START_VALUE ? 0 : timeTick;
			start = start == INITIAL_START_VALUE ? System.currentTimeMillis() : start;
			tickEnd = System.currentTimeMillis();
			long diff = Math.round(((++timeTick) * mspp) - tickEnd + start);
			if (diff > 0) {
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					break;
				}
			}
		} 
	}

	public void setTempoBPM(double bpm) {
		tempoBPM = bpm;
		mspp = 60.0 / PPQ / tempoBPM * 1000;
		start = INITIAL_START_VALUE;
	}

	/*
	 * This is for testing only
	 */

	public static void main(String[] args) {
		final java.util.concurrent.atomic.AtomicLong begin = new java.util.concurrent.atomic.AtomicLong(0);
		final java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger(0);
		final Pulse gen = new Pulse();
		//creation of function take 150-200ms ?!
		Function<Long, Boolean> pulseFunc = (tick) -> {
			if (count.getAndIncrement() < 5) {
				System.out.print(tick);
				System.out.print(' ');
				System.out.println(System.currentTimeMillis() - begin.get());
			}
			if (tick == (PPQ * 120)) {
				System.out.println("double time!");
				gen.setTempoBPM(240.0); //double time
			}
			//2 minutes = 240 beats @ 120 bpm
			//2 minutes = 120 beats @ 120 bpm + 240 beats @ 240 bpm
			return tick < (PPQ * 360);
		};
		begin.set(System.currentTimeMillis());
		gen.pulse(pulseFunc);
		long end = System.currentTimeMillis();
		System.out.println(end - begin.get());
	}

}

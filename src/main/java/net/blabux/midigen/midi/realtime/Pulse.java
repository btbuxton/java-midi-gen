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
	private static final long NANOS_PER_MS = (long)Math.pow(10,6);

	private volatile double tempoBPM;
	private volatile double nspp;
	private volatile long start;
	

	public Pulse() {
		this(DEFAULT_TEMPO_BPM);
	}

	public Pulse(double bpm) {
		setTempoBPM(bpm);
	}

	public long ticks(double beats) {
		return (long)(beats * PPQ);
	}
	
	public void run(final Function<Long, Boolean> pulseFunc) {
		long tick = 0;
		long timeTick = 0;
		long tickEnd;
		while(pulseFunc.apply(tick++)) {
			timeTick = start == INITIAL_START_VALUE ? 0 : timeTick;
			start = start == INITIAL_START_VALUE ? System.nanoTime() : start;
			tickEnd = System.nanoTime();
			long diff = Math.round(((++timeTick) * nspp) - tickEnd + start);
			if (diff > 0) {
				try {
					Thread.sleep(diff / NANOS_PER_MS, (int)(diff % NANOS_PER_MS));
				} catch (InterruptedException e) {
					break;
				}
			}
		} 
	}

	public void setTempoBPM(double bpm) {
		tempoBPM = bpm;
		nspp = 60.0 / PPQ / tempoBPM * 1000 * NANOS_PER_MS;
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
		final long one_min = gen.ticks(120);
		//2 minutes = 240 beats @ 120 bpm
		//2 minutes = 120 beats @ 120 bpm + 240 beats @ 240 bpm
		final long two_min = gen.ticks(360);
		Function<Long, Boolean> pulseFunc = (tick) -> {
			if (count.getAndIncrement() < 5) {
				System.out.print(tick);
				System.out.print(' ');
				System.out.println(System.currentTimeMillis() - begin.get());
			}
			if (tick == one_min) {
				System.out.println("double time!");
				gen.setTempoBPM(240.0); //double time
			}
			
			return tick < two_min;
		};
		begin.set(System.currentTimeMillis());
		gen.run(pulseFunc);
		long end = System.currentTimeMillis();
		System.out.println(end - begin.get());
	}

}

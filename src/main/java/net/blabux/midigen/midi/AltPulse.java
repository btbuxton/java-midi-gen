package net.blabux.midigen.midi;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * An experiment with scheduled thread pools
 * It's not as accurate as Pulse and is more code...Will delete...maybe
 * @author btbuxton
 *
 */
public class AltPulse {
	private static final double DEFAULT_TEMPO_BPM = 120.0;
	private static final int PPQ = 24;
	private static final long MS = 1000000; // nanoseconds per ms

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
	private double tempoBPM;
	private volatile double mspp;

	public AltPulse() {
		this(DEFAULT_TEMPO_BPM);
	}

	public AltPulse(double bpm) {
		setTempoBPM(bpm);
	}

	public void pulse(final Function<Long, Boolean> pulseFunc) {
		final Object lock = new Object();
		new TickRunnable(lock, executor, (long) (mspp * MS), pulseFunc).run();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				
			}
		}
		executor.shutdownNow();
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
		new AltPulse().pulse((tick) -> {
			return tick < (PPQ * 240);
		});
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

	static class TickRunnable implements Runnable {
		private final ScheduledExecutorService executor;
		private final Function<Long, Boolean> pulseFunc;
		private final Object lock;
		private long periodNS;
		private long tick;

		public TickRunnable(Object lock, ScheduledExecutorService executor, long periodNS, Function<Long, Boolean> pulseFunc) {
			this.lock = lock;
			this.executor = executor;
			this.pulseFunc = pulseFunc;
			this.periodNS = periodNS;
			this.tick = 0;
		}

		@Override
		public void run() {
			if (pulseFunc.apply(tick++)) {
				executor.schedule(this, this.periodNS, TimeUnit.NANOSECONDS);
			} else {
				synchronized(lock) {
					lock.notifyAll();
				}
			}
		}

	}
}

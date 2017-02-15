package net.blabux.midigen.example;

import net.blabux.midigen.midi.PulseIterator;

public class MultiThreadPoly {

	public static void main(String[] args) {
		PulseIterator iter = new PulseIterator(90.0);
		Object lock = new Object();
		while(true) {
			long tick = iter.next();
			synchronized(lock) {
				lock.notifyAll();
			}
		}
	}
	
	static class Ticker {
		
	}
}

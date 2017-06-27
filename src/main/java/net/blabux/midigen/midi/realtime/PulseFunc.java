package net.blabux.midigen.midi.realtime;

@FunctionalInterface
public interface PulseFunc {
	boolean pulse(long tick);
}

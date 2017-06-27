package net.blabux.midigen.midi.realtime;

@FunctionalInterface
public interface PulseFunc {
	boolean pulse(long tick);
	default PulseFunc andThen(PulseFunc another) {
		return (tick) -> {
			return this.pulse(tick) && another.pulse(tick);
		};
	}
}

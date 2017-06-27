package net.blabux.midigen.midi.realtime;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;

import net.blabux.midigen.midi.MidiUtil;
import net.blabux.midigen.midi.lfo.LFO;
import net.blabux.midigen.midi.lfo.LFOSine;

public class PulseFuncLFO implements PulseFunc {
	private final Channel channel;
	private final LFO lfo;
	private final int cc;

	/**
	 * Create new PulseFunc that will output values from LFO passed in
	 * 
	 * @param channel
	 * @param lfo
	 */
	public PulseFuncLFO(Channel channel, LFO lfo, int cc) {
		this.channel = channel;
		this.lfo = lfo;
		this.cc = cc;
	}

	@Override
	public boolean pulse(long tick) {
		int nextValue = lfo.next();
		channel.cc(cc, nextValue);
		return true;
	}

	public static void main(String[] args) {

		try {
			MidiUtil.getMidiReceiverNames().forEach(System.out::println);
			final MidiDevice device = MidiUtil.getMidiReceiversContainingNameOrDefault(System.getProperty("recv", ""));
			System.out.println("Using: " + device.getDeviceInfo().getName());
			device.open();
			try {
				try (final Receiver recv = device.getReceiver()) {
					Channel channel = new Channel(recv, 0);
					final PulseGen pulse = new PulseGen(120, 240);
					LFO lfo = new LFOSine(pulse.ticks(1), 0.25, 64, 64);
					PulseFuncLFO pulseFunc = new PulseFuncLFO(channel, lfo, 1);
					final long one_min = pulse.ticks(120);
					PulseFunc pulseAccept = (tick) -> {
						return pulseFunc.pulse(tick) && tick < one_min;
					};
					final Clock clock = new Clock(recv, pulse);
					pulse.run(clock.andThen(pulseAccept));

				}
			} finally {
				device.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Done!");
	}
}

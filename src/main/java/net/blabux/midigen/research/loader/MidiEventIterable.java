package net.blabux.midigen.research.loader;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;

public class MidiEventIterable implements Iterable<MidiEvent> {
	private final Track track;

	public MidiEventIterable(Track track) {
		this.track = track;
	}

	@Override
	public java.util.Iterator<MidiEvent> iterator() {
		return new java.util.Iterator<MidiEvent>() {
			int index = 0;

			@Override
			public MidiEvent next() {
				return track.get(index++);
			}

			@Override
			public boolean hasNext() {
				return index < track.size();
			}
		};
	}
}

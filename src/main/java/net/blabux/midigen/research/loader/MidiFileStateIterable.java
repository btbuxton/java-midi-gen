package net.blabux.midigen.research.loader;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

public class MidiFileStateIterable implements Iterable<MidiEvent> {
	final CurrentState state;
	final Sequence seq;
	
	public MidiFileStateIterable(URL url) throws InvalidMidiDataException, IOException {
		this.seq = MidiSystem.getSequence(url);
		this.state = new CurrentState();
		state.setLength(seq.getTickLength());
		state.setPPQ(seq.getResolution());
	}
	
	@Override
	public Iterator<MidiEvent> iterator() {
		return stateIterator();
	}
	
	public StateIterator stateIterator() {
		List<Iterable<MidiEvent>> iterables = Arrays.asList(seq.getTracks()).stream()
				.map(MidiEventIterable::new).collect(Collectors.toList());
		MergeIterable<MidiEvent> allIter = new MergeIterable<MidiEvent>(MidiEvent::getTick, iterables);
		return new StateIterator(allIter.iterator());
	}
	
	/**
	 * Don't like this being an inner class...it should be flipped...
	 * TODO: REFACTOR to make this root class and not inner
	 * @author btbuxton
	 *
	 */
	public class StateIterator extends PeekableIterator<MidiEvent> {

		public StateIterator(Iterator<MidiEvent> innerIterator) {
			super(innerIterator);
		}
		
		public MidiEvent next() {
			MidiEvent result = super.next();
			state.update(result);
			return result;
		}
		
		public CurrentState getState() {
			return state;
		}
		
		public MidiEvent fastForward(final long ffTick) {
			return fastForward((event) -> event.getTick() >= ffTick);
		}
		
		public MidiEvent fastForward(Predicate<MidiEvent> untilFunc) {
			Optional<MidiEvent> result = Optional.empty();
			do {
				if (hasNext()) {
					result = Optional.of(next());
				} else {
					break;
				}
			} while (!untilFunc.test(result.get()));
			return result.get();
		}
		
	}

}

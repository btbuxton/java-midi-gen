package net.blabux.midigen.research.loader;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SequenceStateIterator extends PeekableIterator<MidiEvent> {
    final CurrentState state;
    final Sequence seq;

    public static Iterable<MidiEvent> iterable(final Sequence seq) {
        return new Iterable<MidiEvent>() {
            @Override
            public Iterator<MidiEvent> iterator() {
                return create(seq);
            }
        };
    }

    public static SequenceStateIterator create(Sequence seq) {
        CurrentState state = new CurrentState();
        state.setLength(seq.getTickLength());
        state.setPPQ(seq.getResolution());
        List<Iterable<MidiEvent>> iterables = Arrays.asList(seq.getTracks()).stream().map(MidiEventIterable::new)
                .collect(Collectors.toList());
        MergeIterable<MidiEvent> allIter = new MergeIterable<MidiEvent>(MidiEvent::getTick, iterables);
        return new SequenceStateIterator(seq, state, allIter.iterator());
    }

    private SequenceStateIterator(Sequence seq, CurrentState state, Iterator<MidiEvent> merged) {
        super(merged);
        this.seq = seq;
        this.state = state;
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

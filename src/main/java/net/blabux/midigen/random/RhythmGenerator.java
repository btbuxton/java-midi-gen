package net.blabux.midigen.random;

import java.util.LinkedList;
import java.util.Random;

/**
 * Random rhythm generator. It's based on the probabilities. I'm still working
 * on this which is why LENGTH_PROBS is hard coded.
 * <p>
 * Would like to do something with the note length because this makes sense to
 * only me and needs more objects to define domain.
 * <p>
 * Length is whatever you want it to be, but 16 is the max length returned so
 * 1/16th notes work best, but don't have to.
 *
 * @author btbuxton
 */
public class RhythmGenerator {
    static final int SIZE = 100;
    static final Random RANDOM = new Random(System.nanoTime());
    static final float[] LENGTH_PROBS = new float[]{0.15f, 0.15f, 0.1f, 0.15f, 0.05f, 0.15f, 0.05f, 0.1f, 0.05f,
            0.05f};
    static final int[] LENGTHS = new int[SIZE];

    public RhythmGenerator() {
        int lengthsIndex = 0;
        for (int index = 0; index < LENGTH_PROBS.length; index++) {
            int times = (int) (LENGTH_PROBS[index] * 100);
            for (int x = 0; x < times; x++) {
                int value = index + 1;
                if (9 == value) {
                    value = 12;
                } else if (10 == value) {
                    value = 16;
                }
                LENGTHS[lengthsIndex++] = value;
            }
        }
    }

    public int nextNoteLength() {
        int index = RANDOM.nextInt(LENGTHS.length);
        return LENGTHS[index];
    }

    public Iterable<Integer> fillBars(int bars, int notesPerBar) {
        LinkedList<Integer> result = new LinkedList<>();
        int max = bars * notesPerBar;
        int amount = 0;
        while (amount < max) {
            int next = nextNoteLength();
            result.addLast(next);
            amount += next;
        }
        amount -= result.removeLast();
        result.addLast(max - amount);
        return result;
    }
}

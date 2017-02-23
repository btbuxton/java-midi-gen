package net.blabux.midigen.common;

import java.util.ArrayList;
import java.util.List;

public class Scale {
	public static enum Mode {Ionian, Dorian, Phrygian, Lydian, Mixolydian, Aeolian, Locrian}
	public final static Scale CHROMATIC = Scale.using(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
	public final static Scale MAJOR = Scale.using(0, 2, 4, 5, 7, 9, 11);
	public final static Scale MINOR_PENT = Scale.using(0, 3, 5, 7, 10);
	public final static Scale MAJOR_PENT = Scale.using(0, 2, 4, 7, 9);

	private final int[] formula;

	public static Scale using(final int... formula) {
		return new Scale(formula);
	}

	public Scale(int[] formula) {
		this.formula = formula;
	}

	public List<Note> notes(Note root) {
		List<Note> result = new ArrayList<>(formula.length);
		final int rootNoteValue = root.getValue();
		for (int each : formula) {
			result.add(Note.ALL.get(rootNoteValue + each));
		}
		return result;
	}
	
	public Scale mode(final Mode mode) {
		final int length = formula.length;
		final int[] result = new int[length];
		final int start = mode.ordinal() % length;
		final int offset = formula[start];
		for (int index = 0; index < length; index++) {
			short value = (short)(formula[(index + start) % length] - offset);
			result[index] = (short)(value < 0 ? value + 12 : value);
		}
		return Scale.using(result);
	}
}

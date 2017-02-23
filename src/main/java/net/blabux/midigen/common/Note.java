package net.blabux.midigen.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Note {
	public final static String[] NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	public final static List<Note> ALL;
	public final static Map<String, Note> BY_NAME;

	private final String name;
	private final int value;
	private final int octave;

	static {
		List<Note> all = create();
		Map<String, Note> nameMapping = new HashMap<>(128);
		for (Note each : all) {
			nameMapping.put(each.toString(), each);
		}
		ALL = Collections.unmodifiableList(all);
		BY_NAME = Collections.unmodifiableMap(nameMapping);
	}

	static List<Note> create() {
		List<Note> result = new ArrayList<>(128);
		short octave = -2;
		Iterable<String> names = Arrays.asList(NAMES);
		Iterator<String> namesIter = names.iterator();
		for (short value = 0; value < 128; value++) {
			if (!namesIter.hasNext()) {
				namesIter = names.iterator();
				octave++;
			}
			String name = namesIter.next();
			result.add(new Note(name, octave, value));
		}
		return result;
	}

	private Note(String name, int octave, int value) {
		this.name = name;
		this.octave = octave;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getOctave() {
		return octave;
	}

	public int getValue() {
		return value;
	}

	public Note octaveUp() {
		return Note.ALL.get(getValue() + 12);
	}

	public Note octaveDown() {
		return Note.ALL.get(getValue() - 12);
	}

	@Override
	public int hashCode() {
		return getValue();
	}

	@Override
	public boolean equals(Object obj) {
		try {
			return equals((Note) obj);
		} catch (ClassCastException ex) {
			return false;
		}
	}

	public boolean equals(Note another) {
		return getValue() == another.getValue();
	}

	public String toString() {
		return getName() + String.valueOf(getOctave());

	}
}

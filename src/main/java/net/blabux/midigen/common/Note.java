package net.blabux.midigen.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Note {
	public final static List<Note> ALL;
	private final static Map<String, Note> BY_NAME;

	private final NoteName name;
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
	
	public static Note named(String name) {
		return BY_NAME.get(name);
	}

	private static List<Note> create() {
		List<Note> result = new ArrayList<>(128);
		int octave = -2;
		Iterable<NoteName> names = Arrays.asList(NoteName.values());
		Iterator<NoteName> namesIter = names.iterator();
		for (short value = 0; value < 128; value++) {
			if (!namesIter.hasNext()) {
				namesIter = names.iterator();
				octave++;
			}
			NoteName name = namesIter.next();
			result.add(new Note(name, octave, value));
		}
		return result;
	}

	private Note(NoteName name, int octave, int value) {
		this.name = name;
		this.octave = octave;
		this.value = value;
	}

	public NoteName getName() {
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

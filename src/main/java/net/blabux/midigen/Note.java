package net.blabux.midigen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Note {
	public final static short[] CHROMATIC = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
	public final static short[] MAJOR = { 0, 2, 4, 5, 7, 9, 11 };
	public final static short[] MINOR_PENT = { 0, 3, 5, 7, 10 };
	public final static short[] MAJOR_PENT = { 0, 2, 4, 7, 9 };
	public final static String[] NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public final static List<Note> ALL;
	public final static Map<String, Note> BY_NAME;
	
	private final String name;
	private final short value;
	
	static {
		List<Note> all = create();
		Map<String,Note> nameMapping = new HashMap<>(128);
		for (Note each : all) {
			nameMapping.put(each.getName(), each);
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
			String name = namesIter.next() + String.valueOf(octave);
			result.add(new Note(name, value));
		}
		return result;
	}

	private Note(String name, short value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public short getValue() {
		return value;
	}
	
	public List<Note> scale(short[] formula) {
		List<Note> result = new ArrayList<>(formula.length);
		for (short each : formula) {
			result.add(Note.ALL.get(getValue() + each));
		}
		return result;
	}

	public Note octaveUp() {
		return Note.ALL.get(getValue() + 12); 
	}
	
	public Note octaveDown() {
		return Note.ALL.get(getValue() - 12); 
	}
}

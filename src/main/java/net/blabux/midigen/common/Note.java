package net.blabux.midigen.common;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Note {
    public final static List<Note> ALL;

    private final static Pattern NAME_PARTS = Pattern.compile("^([A-G][sf\\#]?)(\\-?[0-9])$");

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
    }

    public static Note named(String name) {
        Matcher matcher = NAME_PARTS.matcher(name);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        NoteName noteName = NoteName.get(matcher.group(1));
        int octave = Integer.parseInt(matcher.group(2));
        return noteName.note(octave);
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

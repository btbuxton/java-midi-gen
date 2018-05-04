package net.blabux.midigen.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum NoteName {
    C("C", "Bs", "B#"),
    Cs("C#", "Df", "Db"),
    D("D"),
    Ds("D#", "Ef", "Eb"),
    E("E", "Ff", "Fb"),
    F("F", "Es", "E#"),
    Fs("F#", "Gf", "Gb"),
    G("G"),
    Gs("G#", "Af", "Ab"),
    A("A"),
    As("A#", "Bf", "Bb"),
    B("B", "Cf", "Cb");

    private static Supplier<Map<String, NoteName>> MAPPING_GETTER;
    private String[] names;

    static {
        MAPPING_GETTER = () -> {
            final Map<String, NoteName> mapping = initializeMapping();
            MAPPING_GETTER = () -> mapping;
            return mapping;
        };
    }

    public static NoteName get(String name) {
        return MAPPING_GETTER.get().get(name);
    }

    private static Map<String, NoteName> initializeMapping() {
        Map<String, NoteName> result = new HashMap<>();
        for (NoteName each : values()) {
            result.put(each.name(), each);
            for (String name : each.names) {
                result.put(name, each);
            }
        }
        return result;
    }

    NoteName(String... names) {
        this.names = names;
    }

    String[] getNames() {
        return names;
    }

    public Note note(int octave) {
        int value = ((octave + 2) * 12) + ordinal();
        if (value < 0 || value > 127) {
            throw new IllegalStateException("Value out of bounds: " + String.valueOf(value));
        }
        return Note.ALL.get(value);
    }
}

package net.blabux.midigen.markov;

import net.blabux.midigen.common.LimitedIterable;
import net.blabux.midigen.common.Note;

import javax.sound.midi.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChainLoader {
    private final int depth;

    public ChainLoader(int depth) {
        this.depth = depth;
    }

    public Chain loadChain(URL url) throws InvalidMidiDataException, IOException {
        Sequence seq = MidiSystem.getSequence(url);
        NoteState root = new NoteState();
        for (Track eachTrack : seq.getTracks()) {
            LimitedIterable<Note> ring = new LimitedIterable<Note>(depth);
            Iterable<Note> notes = noteOns(eachTrack);
            for (Note each : notes) {
                NoteState current = root;
                ring.add(each);
                for (Note stateNote : ring) {
                    current = current.getLink(stateNote).addWeight().getTarget();
                }
            }
        }
        return root.toMarkovChain();
    }

    private Iterable<Note> noteOns(Track track) {
        List<Note> notes = new ArrayList<>();
        for (int index = 0; index < track.size(); index++) {
            MidiEvent event = track.get(index);
            MidiMessage msg = event.getMessage();
            // first byte 0x8- = Note off 0x9- = Note on
            int status = 0xF0 & msg.getMessage()[0];
            // only care about note on messages =)
            if (status == 0x90) {
                notes.add(Note.ALL.get(msg.getMessage()[1] & 0xFF));
            }
        }
        return notes;
    }
}

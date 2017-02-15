package net.blabux.midigen.research;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import net.blabux.midigen.common.Note;
import net.blabux.midigen.common.LimitedIterable;
import net.blabux.midigen.markov.Chain;
import net.blabux.midigen.markov.NoteState;

/**
 * Experiment for loading midi files and playing around with them
 * @author btbuxton
 *
 */
public class MidiFileLoader {

	public static void main(String[] args) {
		MidiFileLoader loader = new MidiFileLoader();
		try {
			loader.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void run() throws InvalidMidiDataException, IOException {
		URL url = getClass().getResource("/RunningLate-DanWheeler.mid");
		Sequence seq = MidiSystem.getSequence(url);
		NoteState root = new NoteState();
		for (Track eachTrack : seq.getTracks()) {
			LimitedIterable<Note> ring = new LimitedIterable<Note>(8);
			Iterable<Note> notes = noteOns(eachTrack);
			for (Note each : notes) {
				NoteState current = root;
				ring.add(each);
				for (Note stateNote : ring) {
					current = current.getLink(stateNote).addWeight().getTarget();
				}
			}                
		}
		Chain chain = root.toMarkovChain();
		for (Note each : chain) {
			System.out.println(each);
		}
	}
	
	Iterable<Note> noteOns(Track track) {
		List<Note> notes = new ArrayList<>();
		for (int index = 0; index < track.size(); index++) {
			MidiEvent event = track.get(index);
			MidiMessage msg = event.getMessage();
			//first byte 0x8- = Note off 0x9- = Note on
			int status = 0xF0 & msg.getMessage()[0];
			//only care about note on messages =)
			if (status == 0x90) {
				notes.add(Note.ALL.get(msg.getMessage()[1] & 0xFF));
			}
		}
		return notes;
	}

	//kept here for my own debugging
	String bytesToString(byte[] message) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int index = 0; index < message.length; index++) {
			if (index != 0) {
				builder.append(' ');
			}
			byte each = message[index];
			builder.append(String.format("%02X", each & 0xFF));
		}
		builder.append("]");
		return builder.toString();
	}

}

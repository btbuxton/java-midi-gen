package net.blabux.midigen.markov;

import java.util.HashMap;
import java.util.Map;

import net.blabux.midigen.common.Note;

public class NoteState {
	final Note note;
	final Map<Note, NoteLink> nextLinks;

	public NoteState() {
		this(null);
	}

	public NoteState(Note note) {
		this.note = note;
		this.nextLinks = new HashMap<>();
	}

	public NoteLink getLink(Note note) {
		NoteLink link = nextLinks.get(note);
		if (null == link) {
			link = new NoteLink(new NoteState(note));
			nextLinks.put(note, link);
		}
		return link;
	}
	
	public Note getNote() {
		return note;
	}
	
	public Chain toMarkovChain() {
		Chain chain = new Chain();
		float total = totalWeight();
		for (NoteLink each : nextLinks.values()) {
			chain.addNoteProbability(each.getTarget().getNote(), each.getWeight() / total, each.getTarget().toMarkovChain());
		}
		return chain;
	}

	private float totalWeight() {
		float totalWeight = 0;
		for (NoteLink each : nextLinks.values()) {
			totalWeight += each.getWeight();
		}
		return totalWeight;
	}
}

package net.blabux.midigen.research;

/*
 * Link between two states for calculating weights
 */
public class NoteLink {
	private int weight;
	private final NoteState target;
	
	public NoteLink(NoteState target) {
		weight = 0;
		this.target = target;
	}
	
	public NoteState getTarget() {
		return target;
	}
	
	public NoteLink addWeight() {
		weight++;
		return this;
	}

	public int getWeight() {
		return weight;
	}

}

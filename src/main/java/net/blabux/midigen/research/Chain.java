package net.blabux.midigen.research;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.blabux.midigen.Note;

public class Chain implements Iterable<Note> {
	private final static int MAX = 1000;
	private final static Random RANDOM = new Random(System.nanoTime());
	private final List<NoteEntry> entries;
	private int index = 0;
	 
	public Chain() {
		entries = new ArrayList<NoteEntry>();
	}

	public void addNoteProbability(Note note, float probability, Chain next) {
		int end = (int)(MAX * probability);
		index += end;
		entries.add(new NoteEntry(note, index, next));
	}
	
	public Iterator<Note> iterator() {
		return new Iterator<Note>() {
			Chain current = Chain.this;
			@Override
			public boolean hasNext() {
				return null != current && !current.isEmpty();
			}

			@Override
			public Note next() {
				NoteEntry entry = current.nextEntry();
				current = entry.next;
				return entry.note;
			}
			
		};
	}

	NoteEntry nextEntry() {
		int index = RANDOM.nextInt(MAX);
		NoteEntry last = null;
		for (NoteEntry each : entries) {
			last = each;
			if (each.begin >= index) {
				return last;
			}
		}
		return last;
	}

	boolean isEmpty() {
		return entries.isEmpty();
	}

	static class NoteEntry {
		final Note note;
		final int begin;
		final Chain next;
		
		public NoteEntry(Note note, int begin, Chain next) {
			this.note = note;
			this.begin = begin;
			this.next = next;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(String.valueOf(note));
			builder.append('-');
			builder.append(String.valueOf(begin));
			return builder.toString();
		}
		
	}
}

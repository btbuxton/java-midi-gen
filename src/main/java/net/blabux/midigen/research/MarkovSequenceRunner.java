package net.blabux.midigen.research;

import java.net.URL;
import java.util.Iterator;

import net.blabux.midigen.InfiniteIterator;
import net.blabux.midigen.Note;
import net.blabux.midigen.RhythmGenerator;

public class MarkovSequenceRunner {

	public static void main(String[] args) {
		MarkovSequenceRunner runner = new MarkovSequenceRunner();
		try {
			runner.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void run() throws Exception {
		URL url = getClass().getResource("/RunningLate-DanWheeler.mid");
		Chain chain = new ChainLoader(16).loadChain(url);
		RhythmGenerator rgen = new RhythmGenerator();
		Iterable<Integer> rhythm = rgen.fillBars(4, 16);
		Iterator<Note> notes = new InfiniteIterator<>(chain);
		for (int each : rhythm) {
			Note note = notes.next();
			System.out.println(each + ":" + note);
		}
	}
	

	/*
	 * Use 16th note chunks for length, use multiple and markov for rhythm
	 * length (probability)
	 * 1  (0.15)- 16th
	 * 2  (0.15)- 8th
	 * 3  (0.1)- 8 dotted
	 * 4  (0.15)- 4th
	 * 5  (0.05)- 4 + 16th
	 * 6  (0.15)- 4 dotted
	 * 7  (0.05)- 4 dotted + 16th 
	 * 8  (0.1)- 1/2 note
	 * 12 (0.05) - 1/2 note dotted
	 * 16 (0.05)- whole
	 */
	
}

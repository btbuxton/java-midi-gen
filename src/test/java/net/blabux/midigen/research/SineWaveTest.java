package net.blabux.midigen.research;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class SineWaveTest {

	@Test
	public void test() {
		List<Double> sineWave = SineWave.values();
		assertEquals(0, (int)(sineWave.get(0) * 100));
		assertEquals(100, (int)(sineWave.get(64) * 100));
		assertEquals(0, (int)(sineWave.get(128) * 100));
		assertEquals(-100, (int)(sineWave.get(192) * 100));
	}

}

package net.blabux.midigen.research;

import java.util.Arrays;

public class GridSequence {
	public static int[] constantVelocities(int[] input, int velocity) {
		int length = input.length * 8;
		int[] result = new int[length];
		int current = 0;
		for (int each : input) {
			int scratch = each;
			int mask = 0b10000000;
			while(mask != 0) {
				if ((scratch & mask) > 0) {
					result[current] = velocity;
				} else {
					result[current] = 0;
				}
				current++;
				mask = (byte)(mask >>> 1);
			}
		}
		return result;
	}
	
	GridSequence(int[] velocities) {
		
	}
	
	public static void main(String[] args) {
		int[] seq = constantVelocities(new int[] {0b10001010, 0b10010110} , 5);
		System.out.println(seq);
	}
}

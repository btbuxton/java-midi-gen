package net.blabux.midigen.research;

import java.util.Arrays;

public class Euclidean {
    public boolean[] generate(int accents, int total) {
        boolean[] result = new boolean[total];
        Arrays.fill(result, false);
        float step = (float) total / accents;
        float current = 0;
        while (current < total) {
            result[(int) current] = true;
            current += step;
        }
        return result;
    }

    public static void main(String[] args) {
        boolean[] result = new Euclidean().generate(7, 12);
        for (int i = 0; i < result.length; i++) {
            System.out.println(String.format("%d: %b", i, result[i]));
        }
    }
}

package net.blabux.midigen.midi.realtime;

/**
 * Creates a pulse of subdivisions per quarter note/beat at supplied BPM
 * Has the ability to change tempo while running see main()
 * <p>
 * To do:
 * 1. Time function and warn if it's longer than a wait cycle
 *
 * @author btbuxton
 */
public class PulseGen {
    private static final long INITIAL_START_VALUE = -1;
    private static final double DEFAULT_TEMPO_BPM = 120.0;
    private static final int DEFAULT_PPQ = 24;
    private static final long NANOS_PER_MS = (long) Math.pow(10, 6);

    private final int ppq;
    private volatile double tempoBPM;
    private volatile double nspp;
    private volatile long start;
    private volatile int lags;

    public PulseGen() {
        this(DEFAULT_TEMPO_BPM);
    }

    public PulseGen(double bpm) {
        this(bpm, DEFAULT_PPQ);
    }

    public PulseGen(double bpm, int ppq) {
        this.ppq = ppq;
        setTempoBPM(bpm);
        lags = 0;
    }

    public long ticks(double beats) {
        return (long) (beats * ppq);
    }

    public void run(final PulseFunc pulseFunc) {
        long tick = 0;
        long timeTick = 0;
        long tickEnd;
        while (pulseFunc.pulse(tick++)) {
            timeTick = start == INITIAL_START_VALUE ? 0 : timeTick;
            start = start == INITIAL_START_VALUE ? System.nanoTime() : start;
            tickEnd = System.nanoTime();
            long diff = Math.round(((++timeTick) * nspp) - tickEnd + start);
            if (diff > 0) {
                try {
                    Thread.sleep(diff / NANOS_PER_MS, (int) (diff % NANOS_PER_MS));
                } catch (InterruptedException e) {
                    break;
                }
            } else if (diff > 0) {
                lags++;
            }
        }
    }

    public void setTempoBPM(double bpm) {
        tempoBPM = bpm;
        nspp = 60.0 / ppq / tempoBPM * 1000 * NANOS_PER_MS;
        start = INITIAL_START_VALUE;
    }

    public int getLags() {
        return lags;
    }

    /*
     * This is for testing only
     */

    public static void main(String[] args) {
        final java.util.concurrent.atomic.AtomicLong begin = new java.util.concurrent.atomic.AtomicLong(0);
        final java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger(0);
        final int bpm = 240;
        final PulseGen gen = new PulseGen(bpm, 240); //right now, 240 is max...run to see lags...should be 0
        //creation of function take 150-200ms ?!
        final long one_min = gen.ticks(bpm);
        //2 minutes = 240 beats @ 120 bpm
        //2 minutes = 120 beats @ 120 bpm + 240 beats @ 240 bpm
        final long two_min = gen.ticks(bpm * 2) + one_min;
        PulseFunc pulseFunc = (tick) -> {
            if (count.getAndIncrement() < 5) {
                System.out.print(tick);
                System.out.print(' ');
                System.out.println(System.currentTimeMillis() - begin.get());
            }
            if (tick == one_min) {
                System.out.println("double time!");
                gen.setTempoBPM(bpm * 2); //double time
            }

            return tick < two_min;
        };
        begin.set(System.currentTimeMillis());
        gen.run(pulseFunc);
        long end = System.currentTimeMillis();
        System.out.println(end - begin.get());
        System.out.println("lags = " + gen.getLags());
    }

}

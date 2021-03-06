package net.blabux.midigen.research;

import javax.sound.midi.*;
import javax.sound.sampled.*;

public class GMInstrumentMain {
    private static final int END_OF_TRACK = 0x2F;

    public static void main(String[] args) {
        GMInstrumentMain main = new GMInstrumentMain();
        try {
            main.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static AudioFormat getAudioFormat() {
        float sampleRate = 44_100.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    void run() throws MidiUnavailableException, InvalidMidiDataException {
        Synthesizer synth = MidiSystem.getSynthesizer();
        SourceDataLine line = null;
        try {
            line = AudioSystem.getSourceDataLine(getAudioFormat());
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        synth.open();
        try {
            final Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
            int index = 0;
            final Instrument[] loaded = synth.getLoadedInstruments();
            for (Instrument inst : loaded) {
                System.out.println("Inst: " + String.valueOf(index++) + " " + inst.getName() + " " + inst.getPatch().getProgram());
            }
            final MidiChannel[] channels = synth.getChannels();

            MidiChannel one = channels[0];
            Instrument oneInst = loaded[0];
            System.out.println("Using: " + oneInst.getName() + " for channel 1");
            one.programChange(oneInst.getPatch().getBank(), oneInst.getPatch().getProgram());
            //portamento
            //one.controlChange(65, 127);
            //one.controlChange(5, 64);


            MidiChannel two = channels[1];
            Instrument twoInst = loaded[52];
            System.out.println("Using: " + twoInst.getName() + " for channel 2");
            two.programChange(twoInst.getPatch().getBank(), twoInst.getPatch().getProgram());

            playSimpleSequence(synth.getReceiver());
        } finally {
            synth.close();
        }
    }

    private void playSimpleSequence(Receiver receiver) throws MidiUnavailableException, InvalidMidiDataException {
        Sequence seq = new Sequence(Sequence.PPQ, 24);
        createTrack(seq, 0, 24);
        createTrack(seq, 1, 24);
        System.out.println("seq tick length: " + seq.getTickLength());

        try {
            final Sequencer seqr = MidiSystem.getSequencer(false);
            seqr.setSequence(seq);
            seqr.setTempoInBPM(120.0f);
            seqr.getTransmitter().setReceiver(receiver);
            seqr.open();
            seqr.setLoopCount(3); // Sequencer.LOOP_CONTINUOUSLY
            seqr.setLoopEndPoint(-1);
            addMetaEventListener(seqr);
            System.out.println("loop start: " + seqr.getLoopStartPoint());
            System.out.println("loop end: " + seqr.getLoopEndPoint());
            System.out.println("sequencer ticks: " + seqr.getTickLength());
            //sleep(100);
            try {
                seqr.start();
                try {
                    synchronized (seqr) {
                        seqr.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //while (seqr.isRunning()) {
                //    sleep(200);
                //}
            } finally {
                seqr.close();
            }
            sleep(200); //so ending it not so abrupt
        } finally {
            receiver.close();
        }

    }

    private void addMetaEventListener(Sequencer seqr) {
        seqr.addMetaEventListener((evt) -> {
            if (END_OF_TRACK == evt.getType()) {
                synchronized (seqr) {
                    seqr.notifyAll();
                }
            }
        });
    }

    void createTrack(Sequence seq, int channel, int noteLength) throws InvalidMidiDataException {
        Track track = seq.createTrack();
        short[] notes = {60, 67, 72, 67};
        int ticks = 0;
        for (short each : notes) {
            MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, channel, each, 100);
            MidiEvent eventOn = new MidiEvent(msgOn, ticks);
            track.add(eventOn);
            MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, each, 0);
            MidiEvent eventOff = new MidiEvent(msgOff, ticks + (int) (noteLength * 0.9));
            track.add(eventOff);
            ticks += noteLength;
        }
        MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, 0, 0);
        MidiEvent eventOff = new MidiEvent(msgOff, ticks);
        track.add(eventOff);
    }

    private void sleep(long ms) {
        synchronized (this) {
            try {
                wait(ms);
            } catch (InterruptedException e) {
                // IGNORE IT
            }
        }
    }
}

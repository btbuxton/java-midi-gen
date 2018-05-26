package net.blabux.midigen.research.record;

import com.sun.media.sound.AudioSynthesizer;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class RecordGMInstrumentMain {
    public static void main(String[] args) {
        RecordGMInstrumentMain main = new RecordGMInstrumentMain();
        try {
            AudioSynthesizer synth = (AudioSynthesizer) MidiSystem.getSynthesizer();
            final SourcePipe srcPipe = new SourcePipe();
            srcPipe.open(synth.getFormat());
            final TargetDataLine tgtPipe = srcPipe.asTargetDataLine();
            //tgtPipe.start();
            new Thread(() -> {
                AudioInputStream input = new AudioInputStream(tgtPipe);
                File fileOut = new File("test-synth-out.wav");
                System.out.println("Start writing");
                try {
                    AudioSystem.write(input, AudioFileFormat.Type.WAVE, fileOut);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    System.out.println("End writing " + System.currentTimeMillis());
                }
            }).start();
            synth.open(srcPipe, null);
            try {
                main.run(synth);
            } finally {
                synth.close();
                srcPipe.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Ending it all " + System.currentTimeMillis());
    }

    void run(Synthesizer synth) throws MidiUnavailableException, InvalidMidiDataException {
        final Instrument[] loaded = synth.getLoadedInstruments();
        final MidiChannel[] channels = synth.getChannels();

        MidiChannel one = channels[0];
        Instrument oneInst = loaded[0];
        System.out.println("Using: " + oneInst.getName() + " for channel 1");
        one.programChange(oneInst.getPatch().getBank(), oneInst.getPatch().getProgram());

        MidiChannel two = channels[1];
        Instrument twoInst = loaded[52];
        System.out.println("Using: " + twoInst.getName() + " for channel 2");
        two.programChange(twoInst.getPatch().getBank(), twoInst.getPatch().getProgram());

        playSimpleSequence(synth.getReceiver());
    }

    private void playSimpleSequence(Receiver receiver) throws MidiUnavailableException, InvalidMidiDataException {
        Sequence seq = new Sequence(Sequence.PPQ, 24);
        createTrack(seq, 0, 24);
        createTrack(seq, 1, 24);
        System.out.println("seq tick length: " + seq.getTickLength());

        try {
            Sequencer seqr = MidiSystem.getSequencer(false);
            seqr.setSequence(seq);
            seqr.setTempoInBPM(120.0f);
            seqr.getTransmitter().setReceiver(receiver);
            seqr.open();
            seqr.setLoopCount(3); // Sequencer.LOOP_CONTINUOUSLY
            seqr.setLoopEndPoint(-1);
            //addMetaEventListener(seqr);

            //sleep(250);
            try {
                seqr.start();
                while (seqr.isRunning()) {
                    sleep(200);
                }
            } finally {
                seqr.close();
            }
            sleep(200); //so ending it not so abrupt
        } finally {
            receiver.close();
        }

    }

    void createTrack(Sequence seq, int channel, int noteLength) throws InvalidMidiDataException {
        Track track = seq.createTrack();
        short[] notes = {60, 67, 72, 67};
        int ticks = 0; //seq.getResolution(); //start one quarter note in
        for (short each : notes) {
            MidiMessage msgOn = new ShortMessage(ShortMessage.NOTE_ON, channel, each, 100);
            MidiEvent eventOn = new MidiEvent(msgOn, ticks);
            track.add(eventOn);
            MidiMessage msgOff = new ShortMessage(ShortMessage.NOTE_OFF, channel, each, 0);
            MidiEvent eventOff = new MidiEvent(msgOff, (int) (noteLength * 0.9));
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

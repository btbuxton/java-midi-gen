package net.blabux.midigen.research;

import javax.sound.midi.*;

public class GMInstrumentMain {
    public static void main(String[] args) {
        GMInstrumentMain main = new GMInstrumentMain();
        try {
            main.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void run() throws MidiUnavailableException, InvalidMidiDataException {
        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();
        try {
            final Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
            int index = 0;
//            System.out.println("SB Instruments:");
//            for (Instrument inst : instruments) {
//                System.out.println("Inst: " + String.valueOf(index++) + " " + inst.getName());
//            }
//            System.out.println("Loaded Instruments:");
//            index = 0;
            final Instrument[] loaded = synth.getLoadedInstruments();
            for (Instrument inst : loaded) {
                System.out.println("Inst: " + String.valueOf(index++) + " " + inst.getName() + " " + inst.getPatch().getProgram());
            }
            final MidiChannel[] channels = synth.getChannels();
//            for (MidiChannel channel : channels) {
//                System.out.println(channel.getProgram());
//            }
            MidiChannel one = channels[0];
            Instrument oneInst = loaded[52];
            System.out.println("Using: " + oneInst.getName() + " for channel 1");
            one.programChange(oneInst.getPatch().getBank(), oneInst.getPatch().getProgram());

            MidiChannel two = channels[1];
            Instrument twoInst = loaded[101];
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
        createTrack(seq, 1, 18);
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
            System.out.println("loop start: " + seqr.getLoopStartPoint());
            System.out.println("loop end: " + seqr.getLoopEndPoint());
            System.out.println("sequencer ticks: " + seqr.getTickLength());
            try {
                seqr.start();
                while (seqr.isRunning()) {
                    sleep(200);
                }
            } finally {
                seqr.close();
            }
            sleep(1000); //so ending it not so abrupt
        } finally {
            receiver.close();
        }

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

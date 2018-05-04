package net.blabux.midigen.midi;

import org.junit.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PitchBendTest {

    //http://www.elvenminstrel.com/music/tuning/reference/pitchbends.shtml
    @Test
    public void pitchBend() {
        double freq = 440.0 * 3 / 2; //perfect fifth
        PitchBend pb = new PitchBend(freq);
        assertEquals(76, pb.midiNote);
        assertEquals(8272, pb.amount);
        assertEquals(80, pb.amount - 8192);
    }

    @Test
    public void pitchBendAgain() {
        double freq = 440.0 * 5 / 3;
        PitchBend pb = new PitchBend(freq);
        assertEquals(78, pb.midiNote);
        assertEquals(7551, pb.amount);
        assertEquals(-641, pb.amount - 8192);
    }

    @Test
    public void pitchBendMidi() {
        double freq = MidiMath.midiToFreq(78);
        PitchBend pb = new PitchBend(freq);
        assertEquals(78, pb.midiNote);
        assertEquals(8192, pb.amount);
    }

    @Test
    public void getAmountForMidiPB() {
        double freq = 440 * 3 / 2; // perfect fifth
        PitchBend pb = new PitchBend(freq);
        byte[] amount = pb.getAmountForMidiPB();
        assertEquals(8272, pb.amount);
        assertEquals(0x50, amount[0]);
        assertEquals(0x40, amount[1]);
    }

    @Test
    public void getAmountForMidiPBCenter() {
        PitchBend pb = new PitchBend(440);
        byte[] amount = pb.getAmountForMidiPB();
        assertEquals(0x00, amount[0]);
        assertEquals(0x40, amount[1]);
    }

    @Test
    public void pitchBendRange() throws InvalidMidiDataException {
        PitchBend pb = new PitchBend(440);
        List<MidiEvent> events = new ArrayList<>();
        pb.setToWholeToneBend(events::add, 0, 0);
        assertEquals(4, events.size());
    }

    @Test
    public void getAmountFor500() {
        PitchBend pb = new PitchBend(500);
        assertEquals(71, pb.midiNote);
        assertEquals(9065, pb.amount);
    }

    @Test
    public void getAmountFor510() {
        PitchBend pb = new PitchBend(510);
        assertEquals(72, pb.midiNote);
        assertEquals(6373, pb.amount);
    }
}

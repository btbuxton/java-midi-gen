package net.blabux.midigen.research;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReadWriteBufferTest {

    @Test
    public void testSimpleReadWrite() {
        byte input[] = new byte[]{1, 2, 3};
        ReadWriteBuffer subject = new ReadWriteBuffer(3);
        assertEquals(3, subject.write(input, 0, 3));
        assertEquals(3, subject.available());
        byte result[] = new byte[3];
        assertEquals(3, subject.read(result, 0, 3));
        assertEquals(0, subject.available());
        assertEquals(input[0], result[0]);
        assertEquals(input[1], result[1]);
        assertEquals(input[2], result[2]);
        assertEquals(0, subject.read(result, 0, 3));
    }

    @Test
    public void testOverwrite() {
        byte input[] = new byte[]{1, 2, 3};
        byte more[] = new byte[]{4, 5, 6};
        ReadWriteBuffer subject = new ReadWriteBuffer(3);
        assertEquals(2, subject.write(input, 0, 2));
        assertEquals(1, subject.write(more, 0, 3));
        byte result[] = new byte[3];
        assertEquals(2, subject.read(result, 0, 2));
        assertEquals(1, subject.read(result, 2, 3));
        assertEquals(input[0], result[0]);
        assertEquals(input[1], result[1]);
        assertEquals(more[0], result[2]);
        assertEquals(0, subject.read(result, 0, 3));
    }
}
